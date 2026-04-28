# ==============================================================================
# Generate-EntityFiles.ps1
#
# Scaffolds all boilerplate files for a single entity inside an existing
# Sakila microservice module (DTOs, Mapper, Repository, Service interface,
# Service implementation, Controller).
#
# Usage:
#   .\Generate-EntityFiles.ps1 `
#       -ServiceName "film-service" `
#       -Pkg         "filmservice" `
#       -Entity      "Film" `
#       -IdType      "Integer"
#
# Parameters:
#   -ServiceName  Folder name of the module   (e.g. film-service)
#   -Pkg          Java sub-package             (e.g. filmservice)
#   -Entity       PascalCase entity name       (e.g. Film)
#   -IdType       Java type for the PK         (Integer | Short)  default: Integer
#   -HasNameField Whether to generate existsByName duplicate check (default: true)
# ==============================================================================

param(
    [Parameter(Mandatory)][string]$ServiceName,
    [Parameter(Mandatory)][string]$Pkg,
    [Parameter(Mandatory)][string]$Entity,
    [string]$IdType = "Integer",
    [bool]$HasNameField = $true
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

# ==============================================================================
# Helpers
# ==============================================================================
function Write-File {
    param([string]$Path, [string]$Content)
    if (Test-Path $Path) {
        Write-Host "  [skipped] $(Split-Path $Path -Leaf)  (already exists)" -ForegroundColor Yellow
        return
    }
    $dir = Split-Path $Path -Parent
    if (-not (Test-Path $dir)) { New-Item -ItemType Directory -Force -Path $dir | Out-Null }
    [System.IO.File]::WriteAllText($Path, $Content, [System.Text.UTF8Encoding]::new($false))
    Write-Host "  [created] $(Split-Path $Path -Leaf)" -ForegroundColor DarkGray
}

# ==============================================================================
# Entity Parser  -  reads the @Entity class and extracts field metadata
# ==============================================================================
function Parse-EntityFields {
    param([string]$EntityFile)
    if (-not (Test-Path $EntityFile)) { return $null }

    $lines  = (Get-Content $EntityFile) -replace "`r", ""
    $fields = [System.Collections.Generic.List[PSCustomObject]]::new()
    $anns   = [System.Collections.Generic.List[string]]::new()

    foreach ($raw in $lines) {
        $t = $raw.Trim()

        if ($t -match '^@') {
            [void]$anns.Add($t)
            continue
        }

        if ($t -match '^private\s+(\S+)\s+(\w+)\s*;') {
            $fType = $Matches[1]
            $fName = $Matches[2]
            if ($fName -eq 'serialVersionUID') { $anns.Clear(); continue }

            # Skip reverse-relation fields (@OneToMany, @OneToOne, @ManyToMany)
            $isReverseRel = @($anns | Where-Object { $_ -match '@OneToMany|@ManyToMany|@OneToOne' }).Count -gt 0
            if ($isReverseRel) { $anns.Clear(); continue }

            $isId        = @($anns | Where-Object { $_ -match '^@Id\b'      }).Count -gt 0
            $isManyToOne = @($anns | Where-Object { $_ -match '@ManyToOne'  }).Count -gt 0
            $isNotNull   = @($anns | Where-Object { $_ -match '@NotNull\b'  }).Count -gt 0
            $mtoAnn      = $anns | Where-Object { $_ -match '@ManyToOne'   } | Select-Object -First 1
            $isReqFK     = $isManyToOne -and ($isNotNull -or ($null -ne $mtoAnn -and $mtoAnn -match 'optional\s*=\s*false'))
            $sizeAnn     = $anns | Where-Object { $_ -match '@Size'        } | Select-Object -First 1
            $sizeMax     = if ($null -ne $sizeAnn -and $sizeAnn -match 'max\s*=\s*(\d+)') { [int]$Matches[1] } else { $null }
            $colDef      = $anns | Where-Object { $_ -match '@ColumnDefault' } | Select-Object -First 1
            $isAutoTS    = ($fType -eq 'Instant') -and ($null -ne $colDef -and $colDef -match 'CURRENT_TIMESTAMP')

            [void]$fields.Add([PSCustomObject]@{
                Type         = $fType
                Name         = $fName
                IsId         = $isId
                IsManyToOne  = $isManyToOne
                IsNotNull    = $isNotNull
                IsRequiredFK = $isReqFK
                SizeMax      = $sizeMax
                IsAutoTS     = $isAutoTS
                IsBoolean    = ($fType -in @('Boolean', 'boolean'))
                IsBigDecimal = ($fType -eq 'BigDecimal')
                IsInstant    = ($fType -eq 'Instant')
                IsByte       = ($fType -in @('Byte', 'byte'))
                IsShortNum   = ($fType -in @('Short', 'short') -and -not $isId -and -not $isManyToOne)
            })
            $anns.Clear()
            continue
        }

        # Non-annotation, non-field line  ->  reset annotation accumulator
        if ($t -ne '' -and $t -notmatch '^//' -and $t -notmatch '^\*' -and $t -notmatch '^/\*') {
            $anns.Clear()
        }
    }
    return $fields
}

# Build field blocks (each a multi-line string) for Request / UpdateRequest DTOs
function Build-RequestFieldBlocks {
    param($Fields)
    $blocks = [System.Collections.Generic.List[string]]::new()

    foreach ($f in $Fields) {
        if ($f.IsId -or $f.IsAutoTS) { continue }
        $lines = [System.Collections.Generic.List[string]]::new()

        if ($f.IsManyToOne) {
            $fkName = $f.Name + 'Id'
            if ($f.IsRequiredFK) { [void]$lines.Add("        @NotNull(message = `"${fkName} must not be null`")") }
            [void]$lines.Add("        @Positive(message = `"${fkName} must be positive`")")
            [void]$lines.Add("        Integer ${fkName}")
        }
        elseif ($f.Type -eq 'String') {
            if ($f.IsNotNull)          { [void]$lines.Add("        @NotBlank(message = `"$($f.Name) must not be blank`")") }
            if ($null -ne $f.SizeMax)  { [void]$lines.Add("        @Size(max = $($f.SizeMax), message = `"$($f.Name) must not exceed $($f.SizeMax) characters`")") }
            [void]$lines.Add("        String $($f.Name)")
        }
        elseif ($f.IsBoolean) {
            if ($f.IsNotNull) { [void]$lines.Add("        @NotNull(message = `"$($f.Name) must not be null`")") }
            [void]$lines.Add("        Boolean $($f.Name)")
        }
        elseif ($f.IsBigDecimal) {
            if ($f.IsNotNull) {
                [void]$lines.Add("        @NotNull(message = `"$($f.Name) must not be null`")")
                [void]$lines.Add("        @DecimalMin(value = `"0.0`", inclusive = false, message = `"$($f.Name) must be positive`")")
            }
            [void]$lines.Add("        BigDecimal $($f.Name)")
        }
        elseif ($f.IsByte) {
            if ($f.IsNotNull) { [void]$lines.Add("        @NotNull(message = `"$($f.Name) must not be null`")"); [void]$lines.Add("        @Min(0)") }
            [void]$lines.Add("        Byte $($f.Name)")
        }
        elseif ($f.IsShortNum) {
            if ($f.IsNotNull) { [void]$lines.Add("        @NotNull(message = `"$($f.Name) must not be null`")"); [void]$lines.Add("        @Min(0)") }
            [void]$lines.Add("        Short $($f.Name)")
        }
        elseif ($f.IsInstant) {
            if ($f.IsNotNull) { [void]$lines.Add("        @NotNull(message = `"$($f.Name) must not be null`")") }
            [void]$lines.Add("        Instant $($f.Name)")
        }
        else {
            if ($f.IsNotNull) { [void]$lines.Add("        @NotNull(message = `"$($f.Name) must not be null`")") }
            [void]$lines.Add("        $($f.Type) $($f.Name)")
        }

        if ($lines.Count -gt 0) { [void]$blocks.Add($lines -join "`n") }
    }
    return $blocks
}

# Build field blocks for Response DTO  (includes id + FK id/name pairs + lastUpdate)
function Build-ResponseFieldBlocks {
    param($Fields, [string]$IdType)
    $blocks = [System.Collections.Generic.List[string]]::new()

    foreach ($f in $Fields) {
        if ($f.IsId) { [void]$blocks.Add("        ${IdType} id"); continue }

        if ($f.IsManyToOne) {
            [void]$blocks.Add("        Integer $($f.Name)Id")
            [void]$blocks.Add("        String $($f.Name)Name")
            continue
        }

        $decl = if    ($f.IsBigDecimal)          { "        BigDecimal $($f.Name)" }
                elseif ($f.IsBoolean)             { "        Boolean $($f.Name)"    }
                elseif ($f.IsByte)                { "        Byte $($f.Name)"       }
                elseif ($f.IsShortNum)            { "        Short $($f.Name)"      }
                elseif ($f.IsInstant)             { "        Instant $($f.Name)"    }
                elseif ($f.Type -eq 'String')     { "        String $($f.Name)"     }
                else                              { "        $($f.Type) $($f.Name)" }
        [void]$blocks.Add($decl)
    }
    return $blocks
}

# Join field blocks into the body string of a Java record (commas between fields)
function Join-FieldBlocks {
    param($Blocks)
    $Blocks = @($Blocks)
    if ($Blocks.Count -eq 0) { return "        // TODO: add fields" }
    $sb = [System.Text.StringBuilder]::new()
    for ($i = 0; $i -lt $Blocks.Count; $i++) {
        if ($i -gt 0) { [void]$sb.Append("`n`n") }
        [void]$sb.Append($Blocks[$i])
        if ($i -lt $Blocks.Count - 1) { [void]$sb.Append(",") }
    }
    return $sb.ToString()
}
# Compute sorted import lines for Request / UpdateRequest DTOs
function Get-RequestImportLines {
    param($Fields)
    $Fields = @($Fields)
    $active = @($Fields | Where-Object { -not $_.IsId -and -not $_.IsAutoTS })
    $needBigDecimal = @($active | Where-Object { $_.IsBigDecimal }).Count -gt 0
    $needInstant    = @($active | Where-Object { $_.IsInstant    }).Count -gt 0
    $needDecimalMin = @($active | Where-Object { $_.IsBigDecimal -and $_.IsNotNull }).Count -gt 0
    $needMin        = @($active | Where-Object { ($_.IsByte -or $_.IsShortNum) -and $_.IsNotNull }).Count -gt 0
    $needNotBlank   = @($active | Where-Object { $_.Type -eq 'String' -and $_.IsNotNull }).Count -gt 0
    $needNotNull    = @($active | Where-Object { ($_.IsBoolean -or $_.IsBigDecimal -or $_.IsByte -or $_.IsShortNum -or $_.IsInstant) -and $_.IsNotNull }).Count -gt 0
    $needNNForFK    = @($active | Where-Object { $_.IsManyToOne -and $_.IsRequiredFK }).Count -gt 0
    $needPositive   = @($active | Where-Object { $_.IsManyToOne }).Count -gt 0
    $needSize       = @($active | Where-Object { $null -ne $_.SizeMax }).Count -gt 0
    $imports = [System.Collections.Generic.List[string]]::new()
    if ($needBigDecimal) { [void]$imports.Add("import java.math.BigDecimal;") }
    if ($needInstant)    { [void]$imports.Add("import java.time.Instant;")    }
    if ($needBigDecimal -or $needInstant) { [void]$imports.Add("") }
    if ($needDecimalMin)               { [void]$imports.Add("import jakarta.validation.constraints.DecimalMin;") }
    if ($needMin)                      { [void]$imports.Add("import jakarta.validation.constraints.Min;")        }
    if ($needNotBlank)                 { [void]$imports.Add("import jakarta.validation.constraints.NotBlank;")   }
    if ($needNotNull -or $needNNForFK) { [void]$imports.Add("import jakarta.validation.constraints.NotNull;")   }
    if ($needPositive)                 { [void]$imports.Add("import jakarta.validation.constraints.Positive;")  }
    if ($needSize)                     { [void]$imports.Add("import jakarta.validation.constraints.Size;")       }
    return $imports
}
# Compute sorted import lines for Response DTO
function Get-ResponseImportLines {
    param($Fields)
    $Fields  = @($Fields)
    $imports = [System.Collections.Generic.List[string]]::new()
    if (@($Fields | Where-Object { $_.IsBigDecimal }).Count -gt 0) { [void]$imports.Add("import java.math.BigDecimal;") }
    if (@($Fields | Where-Object { $_.IsInstant    }).Count -gt 0) { [void]$imports.Add("import java.time.Instant;")    }
    return $imports
}
# Build @Mapping lines for the Mapper interface based on FK fields
function Build-MapperMappings {
    param($Fields)
    $Fields     = @($Fields)
    $toEntity   = [System.Collections.Generic.List[string]]::new()
    $toResponse = [System.Collections.Generic.List[string]]::new()
    foreach ($f in @($Fields | Where-Object { $_.IsManyToOne })) {
        [void]$toEntity.Add("@Mapping(target = `"$($f.Name).id`", source = `"$($f.Name)Id`")")
        [void]$toResponse.Add("@Mapping(source = `"$($f.Name).id`",   target = `"$($f.Name)Id`")")
        [void]$toResponse.Add("@Mapping(source = `"$($f.Name).name`", target = `"$($f.Name)Name`")")
    }
    return @{ ToEntity = $toEntity; ToResponse = $toResponse }
}

# ==============================================================================
# Derived values
# ==============================================================================
$Root     = $PSScriptRoot
$SvcRoot  = Join-Path $Root $ServiceName
$JavaBase = "com\me\learning\parent\$Pkg"
$JavaSrc  = Join-Path $SvcRoot "src\main\java\$JavaBase"
$JavaTest = Join-Path $SvcRoot "src\test\java\$JavaBase"
$NewPkg   = "com.me.learning.parent.$Pkg"

# camelCase and plural forms
$camelName  = $Entity.Substring(0, 1).ToLower() + $Entity.Substring(1)
$pluralName = $camelName + "s"

# ID literal used in test stubs  (Short needs a cast)
$idLiteral     = if ($IdType -eq "Short") { "(short) 1" } else { "1" }
$idMatcher     = if ($IdType -eq "Short") { "eq((short) 1)" } else { "eq(1)" }
$idPathLiteral = "1"

$today = (Get-Date).ToString("dd/MM/yyyy")

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Generating files for entity: $Entity  (camel: $camelName)" -ForegroundColor Cyan
Write-Host "  Module : $ServiceName  ($NewPkg)" -ForegroundColor Cyan
Write-Host "  ID type: $IdType" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# ==============================================================================
# Parse entity file (if it already exists)
# ==============================================================================
$EntityFile   = Join-Path $JavaSrc "entity\${Entity}.java"
$EntityFields = @(Parse-EntityFields -EntityFile $EntityFile | Where-Object { $_ -ne $null })
$HasEntity    = $EntityFields.Count -gt 0

if ($HasEntity) {
    $fkFields = @($EntityFields | Where-Object { $_.IsManyToOne })
    Write-Host ""
    Write-Host "  Entity file found - generating DTOs from $($EntityFields.Count) field(s)" -ForegroundColor Green
    if ($fkFields.Count -gt 0) {
        Write-Host "  FK fields detected: $($fkFields.Name -join ', ')" -ForegroundColor Green
    }
} else {
    Write-Host ""
    Write-Host "  Entity file not found - generating stub DTOs" -ForegroundColor Yellow
    Write-Host "  Tip: create ${Entity}.java first, then re-run to get full DTOs" -ForegroundColor Yellow
}

# ==============================================================================
# DTO  -  Request
# ==============================================================================
if ($HasEntity) {
    $reqImportLines = @(Get-RequestImportLines -Fields $EntityFields)
    $reqImportBlock = if ($reqImportLines.Count -gt 0) { "`n" + ($reqImportLines -join "`n") + "`n" } else { "" }
    $reqBody        = Join-FieldBlocks -Blocks (Build-RequestFieldBlocks -Fields $EntityFields)
} else {
    $reqImportBlock = @"

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
"@
    $reqBody = @"
        @NotBlank(message = "${Entity} name must not be blank")
        @Size(max = 50, message = "${Entity} name must not exceed 50 characters")
        String name

        // TODO: add FK fields as:
        //   @NotNull(message = "ParentId must not be null")
        //   @Positive(message = "ParentId must be a positive number")
        //   Integer parentId
"@
}

$DtoRequest = @"
package $NewPkg.dto;
$reqImportBlock
/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : $today
 * Usage    : Create request DTO for ${Entity} entity
 * Since    : Version 1.0
 */
public record ${Entity}Request(

$reqBody

) {
}
"@
Write-File -Path (Join-Path $JavaSrc "dto\${Entity}Request.java") -Content $DtoRequest

# ==============================================================================
# DTO  -  UpdateRequest
# ==============================================================================
$DtoUpdate = @"
package $NewPkg.dto;
$reqImportBlock
/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : $today
 * Usage    : Update request DTO for ${Entity} entity
 * Since    : Version 1.0
 */
public record ${Entity}UpdateRequest(

$reqBody

) {
}
"@
Write-File -Path (Join-Path $JavaSrc "dto\${Entity}UpdateRequest.java") -Content $DtoUpdate

# ==============================================================================
# DTO  -  Response
# ==============================================================================
if ($HasEntity) {
    $respImportLines = @(Get-ResponseImportLines -Fields $EntityFields)
    $respImportBlock = if ($respImportLines.Count -gt 0) { "`n" + ($respImportLines -join "`n") + "`n" } else { "" }
    $respBody        = Join-FieldBlocks -Blocks (Build-ResponseFieldBlocks -Fields $EntityFields -IdType $IdType)
} else {
    $respImportBlock = @"

import java.time.Instant;
"@
    $respBody = @"
        $IdType id,
        String name,
        Instant lastUpdate

        // TODO: add FK response fields (e.g. Integer parentId, String parentName)
"@
}

$DtoResponse = @"
package $NewPkg.dto;
$respImportBlock
/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : $today
 * Usage    : Response DTO for ${Entity} entity
 * Since    : Version 1.0
 */
public record ${Entity}Response(

$respBody

) {
}
"@
Write-File -Path (Join-Path $JavaSrc "dto\${Entity}Response.java") -Content $DtoResponse

# ==============================================================================
# Mapper
# ==============================================================================
$mapperMappings      = if ($HasEntity) { Build-MapperMappings -Fields $EntityFields } else { @{ ToEntity = @(); ToResponse = @() } }
$toEntityMappings    = if ($mapperMappings.ToEntity.Count   -gt 0) { ($mapperMappings.ToEntity   -join "`n    ") + "`n    " } else { "// TODO: add @Mapping annotations for FK fields`n    " }
$toResponseMappings  = if ($mapperMappings.ToResponse.Count -gt 0) { ($mapperMappings.ToResponse -join "`n    ") + "`n    " } else { "// TODO: add @Mapping annotations for FK fields`n    " }

$Mapper = @"
package $NewPkg.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import $NewPkg.dto.${Entity}Request;
import $NewPkg.dto.${Entity}Response;
import $NewPkg.dto.${Entity}UpdateRequest;
import $NewPkg.entity.${Entity};

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : $today
 * Usage    : MapStruct mapper for ${Entity} entity
 * Since    : Version 1.0
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ${Entity}Mapper {

    ${toEntityMappings}${Entity} toEntity (${Entity}Request request);

    ${toResponseMappings}${Entity}Response toResponse (${Entity} entity);

    List<${Entity}Response> toResponseList (List<${Entity}> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity (${Entity}UpdateRequest request, @MappingTarget ${Entity} entity);
}
"@
Write-File -Path (Join-Path $JavaSrc "mapper\${Entity}Mapper.java") -Content $Mapper

# ==============================================================================
# Repository
# ==============================================================================
$ExtraQueryMethod = if ($HasNameField) {
    @"

    Optional<${Entity}> findByNameIgnoreCase (String name);

    boolean existsByNameIgnoreCase (String name);
"@
} else { "" }

$Repository = @"
package $NewPkg.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import $NewPkg.entity.${Entity};

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : $today
 * Usage    : JPA Repository for ${Entity} entity
 * Since    : Version 1.0
 */
@Repository
public interface ${Entity}Repository extends JpaRepository<${Entity}, $IdType> {
$ExtraQueryMethod
    // TODO: add domain-specific query methods
}
"@
Write-File -Path (Join-Path $JavaSrc "repository\${Entity}Repository.java") -Content $Repository

# ==============================================================================
# Service Interface
# ==============================================================================
$ServiceInterface = @"
package $NewPkg.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import $NewPkg.dto.${Entity}Request;
import $NewPkg.dto.${Entity}Response;
import $NewPkg.dto.${Entity}UpdateRequest;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : $today
 * Usage    : Service interface for ${Entity} entity operations
 * Since    : Version 1.0
 */
public interface ${Entity}Service {

    /** Create a new ${camelName}. */
    ${Entity}Response create${Entity} (${Entity}Request request);

    /** Fully update an existing ${camelName}. */
    ${Entity}Response update${Entity} ($IdType id, ${Entity}UpdateRequest request);

    /** Partially update an existing ${camelName}. */
    ${Entity}Response patch${Entity} ($IdType id, ${Entity}UpdateRequest request);

    /** Retrieve a ${camelName} by its ID. */
    ${Entity}Response get${Entity}ById ($IdType id);

    /** Retrieve all ${pluralName}. */
    List<${Entity}Response> getAll${Entity}s ();

    /** Retrieve all ${pluralName} with pagination. */
    Page<${Entity}Response> getAll${Entity}s (Pageable pageable);

    /** Delete a ${camelName} by ID. */
    void delete${Entity} ($IdType id);

    /** Check whether a ${camelName} exists by ID. */
    boolean existsById ($IdType id);

    /** Count total ${pluralName}. */
    long count${Entity}s ();
}
"@
Write-File -Path (Join-Path $JavaSrc "service\${Entity}Service.java") -Content $ServiceInterface

# ==============================================================================
# Service Implementation
# ==============================================================================
$DuplicateCheck = if ($HasNameField) {
    @"

        if (repository.existsByNameIgnoreCase (request.name ())) {
            throw new DuplicateResourceException (RESOURCE, FIELD_NAME, request.name ());
        }
"@
} else { "" }

$ServiceImpl = @"
package $NewPkg.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import $NewPkg.dto.${Entity}Request;
import $NewPkg.dto.${Entity}Response;
import $NewPkg.dto.${Entity}UpdateRequest;
import $NewPkg.entity.${Entity};
import com.me.learning.framework.web.errors.DuplicateResourceException;
import com.me.learning.framework.web.errors.ResourceNotFoundException;
import $NewPkg.mapper.${Entity}Mapper;
import $NewPkg.repository.${Entity}Repository;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : $today
 * Usage    : Service implementation for ${Entity} entity operations
 * Since    : Version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional (readOnly = true)
public class ${Entity}ServiceImpl implements ${Entity}Service {

    private static final String RESOURCE   = "${Entity}";
    private static final String FIELD_ID   = "id";
    private static final String FIELD_NAME = "name";

    private final ${Entity}Repository repository;
    private final ${Entity}Mapper mapper;

    @Override
    @Transactional
    @CacheEvict (value = {"$pluralName", "$camelName"}, allEntries = true)
    public ${Entity}Response create${Entity} (${Entity}Request request) {
        log.debug ("Creating ${camelName}: {}", request.name ());
$DuplicateCheck
        ${Entity} entity = mapper.toEntity (request);
        ${Entity} saved  = repository.save (entity);

        log.info ("Created ${camelName} with ID: {}", saved.getId ());
        return mapper.toResponse (saved);
    }

    @Override
    @Transactional
    @CacheEvict (value = {"$pluralName", "$camelName"}, allEntries = true)
    public ${Entity}Response update${Entity} ($IdType id, ${Entity}UpdateRequest request) {
        log.debug ("Updating ${camelName} with ID: {}", id);

        ${Entity} existing = repository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (RESOURCE, FIELD_ID, id));

        // TODO: map fields from request to existing entity
        // existing.setName (request.name ());

        ${Entity} updated = repository.save (existing);
        log.info ("Updated ${camelName} with ID: {}", id);
        return mapper.toResponse (updated);
    }

    @Override
    @Transactional
    @CacheEvict (value = {"$pluralName", "$camelName"}, allEntries = true)
    public ${Entity}Response patch${Entity} ($IdType id, ${Entity}UpdateRequest request) {
        log.debug ("Patching ${camelName} with ID: {}", id);

        ${Entity} existing = repository.findById (id)
                .orElseThrow (() -> new ResourceNotFoundException (RESOURCE, FIELD_ID, id));

        mapper.updateEntity (request, existing);
        return mapper.toResponse (repository.save (existing));
    }

    @Override
    public ${Entity}Response get${Entity}ById ($IdType id) {
        log.debug ("Fetching ${camelName} with ID: {}", id);
        return mapper.toResponse (
                repository.findById (id)
                        .orElseThrow (() -> new ResourceNotFoundException (RESOURCE, FIELD_ID, id)));
    }

    @Override
    public List<${Entity}Response> getAll${Entity}s () {
        log.debug ("Fetching all ${pluralName}");
        return mapper.toResponseList (repository.findAll ());
    }

    @Override
    public Page<${Entity}Response> getAll${Entity}s (Pageable pageable) {
        log.debug ("Fetching ${pluralName} - page {}, size {}",
                pageable.getPageNumber (), pageable.getPageSize ());
        return repository.findAll (pageable).map (mapper::toResponse);
    }

    @Override
    @Transactional
    @CacheEvict (value = {"$pluralName", "$camelName"}, allEntries = true)
    public void delete${Entity} ($IdType id) {
        log.debug ("Deleting ${camelName} with ID: {}", id);

        if ( !repository.existsById (id) ) {
            throw new ResourceNotFoundException (RESOURCE, FIELD_ID, id);
        }

        repository.deleteById (id);
        log.info ("Deleted ${camelName} with ID: {}", id);
    }

    @Override
    public boolean existsById ($IdType id) {
        return repository.existsById (id);
    }

    @Override
    public long count${Entity}s () {
        return repository.count ();
    }
}
"@
Write-File -Path (Join-Path $JavaSrc "service\${Entity}ServiceImpl.java") -Content $ServiceImpl

# ==============================================================================
# REST Controller
# ==============================================================================
$Controller = @"
package $NewPkg.controller;

import java.util.List;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import $NewPkg.dto.${Entity}Request;
import $NewPkg.dto.${Entity}Response;
import $NewPkg.dto.${Entity}UpdateRequest;
import $NewPkg.service.${Entity}Service;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : $today
 * Usage    : REST Controller for ${Entity} operations
 * Since    : Version 1.0
 */
@Slf4j
@RestController
@RequestMapping ("/api/v1/$pluralName")
@RequiredArgsConstructor
@Tag (name = "${Entity}", description = "${Entity} management APIs")
public class ${Entity}Controller {

    private final ${Entity}Service service;

    @PostMapping
    @Operation (summary = "Create a new ${camelName}")
    @ApiResponses ({
            @ApiResponse (responseCode = "201", description = "${Entity} created",
                    content = @Content (schema = @Schema (implementation = ${Entity}Response.class))),
            @ApiResponse (responseCode = "400", description = "Invalid input"),
            @ApiResponse (responseCode = "409", description = "${Entity} already exists")
    })
    public ResponseEntity<${Entity}Response> create${Entity} (
            @Valid @RequestBody final ${Entity}Request request) {
        log.info ("REST request to create ${Entity}");
        return ResponseEntity.status (HttpStatus.CREATED).body (service.create${Entity} (request));
    }

    @PutMapping ("/{id}")
    @Operation (summary = "Fully update a ${camelName} by ID")
    @ApiResponses ({
            @ApiResponse (responseCode = "200", description = "${Entity} updated"),
            @ApiResponse (responseCode = "404", description = "${Entity} not found")
    })
    public ResponseEntity<${Entity}Response> update${Entity} (
            @Parameter (description = "${Entity} ID") @PathVariable $IdType id,
            @Valid @RequestBody ${Entity}UpdateRequest request) {
        log.info ("REST request to update ${Entity} with ID: {}", id);
        return ResponseEntity.ok (service.update${Entity} (id, request));
    }

    @PatchMapping ("/{id}")
    @Operation (summary = "Partially update a ${camelName} by ID")
    @ApiResponse (responseCode = "200", description = "${Entity} patched")
    public ResponseEntity<${Entity}Response> patch${Entity} (
            @Parameter (description = "${Entity} ID") @PathVariable $IdType id,
            @RequestBody ${Entity}UpdateRequest request) {
        log.info ("REST request to patch ${Entity} with ID: {}", id);
        return ResponseEntity.ok (service.patch${Entity} (id, request));
    }

    @GetMapping ("/{id}")
    @Operation (summary = "Get a ${camelName} by ID")
    @ApiResponses ({
            @ApiResponse (responseCode = "200", description = "${Entity} found"),
            @ApiResponse (responseCode = "404", description = "${Entity} not found")
    })
    public ResponseEntity<${Entity}Response> get${Entity}ById (
            @Parameter (description = "${Entity} ID") @PathVariable $IdType id) {
        log.info ("REST request to get ${Entity} with ID: {}", id);
        return ResponseEntity.ok (service.get${Entity}ById (id));
    }

    @GetMapping
    @Operation (summary = "Get all ${pluralName} with pagination")
    @ApiResponse (responseCode = "200", description = "${Entity}s retrieved")
    public ResponseEntity<Page<${Entity}Response>> getAll${Entity}s (
            @PageableDefault (size = 20) Pageable pageable) {
        log.info ("REST request to get all ${Entity}s");
        return ResponseEntity.ok (service.getAll${Entity}s (pageable));
    }

    @GetMapping ("/all")
    @Operation (summary = "Get all ${pluralName} as a list")
    @ApiResponse (responseCode = "200", description = "${Entity}s retrieved")
    public ResponseEntity<List<${Entity}Response>> getAll${Entity}sList () {
        log.info ("REST request to get all ${Entity}s as list");
        return ResponseEntity.ok (service.getAll${Entity}s ());
    }

    @DeleteMapping ("/{id}")
    @Operation (summary = "Delete a ${camelName} by ID")
    @ApiResponses ({
            @ApiResponse (responseCode = "204", description = "${Entity} deleted"),
            @ApiResponse (responseCode = "404", description = "${Entity} not found")
    })
    public ResponseEntity<Void> delete${Entity} (
            @Parameter (description = "${Entity} ID") @PathVariable $IdType id) {
        log.info ("REST request to delete ${Entity} with ID: {}", id);
        service.delete${Entity} (id);
        return ResponseEntity.noContent ().build ();
    }

    @GetMapping ("/count")
    @Operation (summary = "Count total ${pluralName}")
    @ApiResponse (responseCode = "200", description = "Count retrieved")
    public ResponseEntity<Long> count${Entity}s () {
        return ResponseEntity.ok (service.count${Entity}s ());
    }

    @GetMapping ("/exists/{id}")
    @Operation (summary = "Check if ${camelName} exists by ID")
    @ApiResponse (responseCode = "200", description = "Check completed")
    public ResponseEntity<Boolean> existsById (
            @Parameter (description = "${Entity} ID") @PathVariable $IdType id) {
        return ResponseEntity.ok (service.existsById (id));
    }
}
"@
Write-File -Path (Join-Path $JavaSrc "controller\${Entity}Controller.java") -Content $Controller

# ==============================================================================
# Service Unit Test
# ==============================================================================
$DuplicateServiceTest = if ($HasNameField) {
    @"

        @Test
        @DisplayName ("should throw DuplicateResourceException when name already exists")
        void create${Entity}_Duplicate () {
            when (repository.existsByNameIgnoreCase (request.name ())).thenReturn (true);

            assertThatThrownBy (() -> service.create${Entity} (request))
                    .isInstanceOf (DuplicateResourceException.class);

            verify (repository, never ()).save (any ());
        }
"@
} else { "" }

$CreateSetupDuplicate = if ($HasNameField) {
    "            when (repository.existsByNameIgnoreCase (request.name ())).thenReturn (false);"
} else { "" }

$ServiceTest = @"
package $NewPkg.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.me.learning.framework.web.errors.DuplicateResourceException;
import com.me.learning.framework.web.errors.ResourceNotFoundException;
import $NewPkg.dto.${Entity}Request;
import $NewPkg.dto.${Entity}Response;
import $NewPkg.dto.${Entity}UpdateRequest;
import $NewPkg.entity.${Entity};
import $NewPkg.mapper.${Entity}Mapper;
import $NewPkg.repository.${Entity}Repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Author   : Prabakaran Ramu
 * Date     : $today
 * Usage    : Unit tests for ${Entity}ServiceImpl
 */
@ExtendWith (MockitoExtension.class)
@DisplayName ("${Entity}ServiceImpl")
class ${Entity}ServiceImplTest {

    @Mock
    private ${Entity}Repository repository;

    @Mock
    private ${Entity}Mapper mapper;

    @InjectMocks
    private ${Entity}ServiceImpl service;

    private ${Entity} entity;
    private ${Entity}Response response;
    private ${Entity}Request request;
    private ${Entity}UpdateRequest updateRequest;

    @BeforeEach
    void setUp () {
        entity = new ${Entity} ();
        entity.setId ($idLiteral);
        entity.setName ("Test${Entity}");

        response = new ${Entity}Response ($idLiteral, "Test${Entity}", Instant.now ());

        request = new ${Entity}Request ("Test${Entity}");

        updateRequest = new ${Entity}UpdateRequest ("Updated${Entity}");
    }

    // -------------------------------------------------------------------------
    @Nested
    @DisplayName ("create${Entity}")
    class Create${Entity} {

        @Test
        @DisplayName ("should create and return ${camelName} when input is valid")
        void create${Entity}_Success () {
$CreateSetupDuplicate
            when (mapper.toEntity (request)).thenReturn (entity);
            when (repository.save (entity)).thenReturn (entity);
            when (mapper.toResponse (entity)).thenReturn (response);

            ${Entity}Response result = service.create${Entity} (request);

            assertThat (result).isNotNull ();
            assertThat (result.name ()).isEqualTo ("Test${Entity}");
            verify (repository).save (entity);
        }
$DuplicateServiceTest
    }

    // -------------------------------------------------------------------------
    @Nested
    @DisplayName ("update${Entity}")
    class Update${Entity} {

        @Test
        @DisplayName ("should update and return ${camelName} when ID exists")
        void update${Entity}_Success () {
            when (repository.findById ($idLiteral)).thenReturn (Optional.of (entity));
            when (repository.save (entity)).thenReturn (entity);
            when (mapper.toResponse (entity)).thenReturn (response);

            ${Entity}Response result = service.update${Entity} ($idLiteral, updateRequest);

            assertThat (result).isNotNull ();
            verify (repository).save (entity);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when ID does not exist")
        void update${Entity}_NotFound () {
            when (repository.findById ($idLiteral)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> service.update${Entity} ($idLiteral, updateRequest))
                    .isInstanceOf (ResourceNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    @Nested
    @DisplayName ("patch${Entity}")
    class Patch${Entity} {

        @Test
        @DisplayName ("should patch and return ${camelName} when ID exists")
        void patch${Entity}_Success () {
            when (repository.findById ($idLiteral)).thenReturn (Optional.of (entity));
            when (repository.save (entity)).thenReturn (entity);
            when (mapper.toResponse (entity)).thenReturn (response);

            ${Entity}Response result = service.patch${Entity} ($idLiteral, updateRequest);

            assertThat (result).isNotNull ();
            verify (mapper).updateEntity (updateRequest, entity);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when ID does not exist")
        void patch${Entity}_NotFound () {
            when (repository.findById ($idLiteral)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> service.patch${Entity} ($idLiteral, updateRequest))
                    .isInstanceOf (ResourceNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    @Nested
    @DisplayName ("get${Entity}ById")
    class Get${Entity}ById {

        @Test
        @DisplayName ("should return ${camelName} when ID exists")
        void get${Entity}ById_Success () {
            when (repository.findById ($idLiteral)).thenReturn (Optional.of (entity));
            when (mapper.toResponse (entity)).thenReturn (response);

            ${Entity}Response result = service.get${Entity}ById ($idLiteral);

            assertThat (result).isNotNull ();
            assertThat (result.id ()).isEqualTo ($idLiteral);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when ID does not exist")
        void get${Entity}ById_NotFound () {
            when (repository.findById ($idLiteral)).thenReturn (Optional.empty ());

            assertThatThrownBy (() -> service.get${Entity}ById ($idLiteral))
                    .isInstanceOf (ResourceNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    @Nested
    @DisplayName ("getAll${Entity}s")
    class GetAll${Entity}s {

        @Test
        @DisplayName ("should return all ${pluralName} as a list")
        void getAll${Entity}s_List () {
            when (repository.findAll ()).thenReturn (List.of (entity));
            when (mapper.toResponseList (List.of (entity))).thenReturn (List.of (response));

            List<${Entity}Response> result = service.getAll${Entity}s ();

            assertThat (result).hasSize (1);
        }

        @Test
        @DisplayName ("should return paginated ${pluralName}")
        void getAll${Entity}s_Paginated () {
            Pageable pageable = PageRequest.of (0, 10);
            Page<${Entity}> page = new PageImpl<> (List.of (entity));
            when (repository.findAll (pageable)).thenReturn (page);
            when (mapper.toResponse (entity)).thenReturn (response);

            Page<${Entity}Response> result = service.getAll${Entity}s (pageable);

            assertThat (result.getContent ()).hasSize (1);
        }
    }

    // -------------------------------------------------------------------------
    @Nested
    @DisplayName ("delete${Entity}")
    class Delete${Entity} {

        @Test
        @DisplayName ("should delete ${camelName} when ID exists")
        void delete${Entity}_Success () {
            when (repository.existsById ($idLiteral)).thenReturn (true);

            service.delete${Entity} ($idLiteral);

            verify (repository).deleteById ($idLiteral);
        }

        @Test
        @DisplayName ("should throw ResourceNotFoundException when ID does not exist")
        void delete${Entity}_NotFound () {
            when (repository.existsById ($idLiteral)).thenReturn (false);

            assertThatThrownBy (() -> service.delete${Entity} ($idLiteral))
                    .isInstanceOf (ResourceNotFoundException.class);

            verify (repository, never ()).deleteById (any ());
        }
    }

    // -------------------------------------------------------------------------
    @Nested
    @DisplayName ("existsById")
    class ExistsById {

        @Test
        @DisplayName ("should return true when ${camelName} exists")
        void existsById_True () {
            when (repository.existsById ($idLiteral)).thenReturn (true);
            assertThat (service.existsById ($idLiteral)).isTrue ();
        }

        @Test
        @DisplayName ("should return false when ${camelName} does not exist")
        void existsById_False () {
            when (repository.existsById ($idLiteral)).thenReturn (false);
            assertThat (service.existsById ($idLiteral)).isFalse ();
        }
    }

    // -------------------------------------------------------------------------
    @Nested
    @DisplayName ("count${Entity}s")
    class Count${Entity}s {

        @Test
        @DisplayName ("should return total ${camelName} count")
        void count${Entity}s_Success () {
            when (repository.count ()).thenReturn (10L);
            assertThat (service.count${Entity}s ()).isEqualTo (10L);
        }
    }
}
"@
Write-File -Path (Join-Path $JavaTest "service\${Entity}ServiceImplTest.java") -Content $ServiceTest

# ==============================================================================
# Controller Integration Test  (REST Assured)
# ==============================================================================
$ControllerIT = @"
package $NewPkg.controller;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import com.me.learning.framework.web.errors.DuplicateResourceException;
import com.me.learning.framework.web.errors.ResourceNotFoundException;
import $NewPkg.dto.${Entity}Response;
import $NewPkg.service.${Entity}Service;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Author   : Prabakaran Ramu
 * Date     : $today
 * Usage    : REST Assured integration tests for ${Entity}Controller
 */
@SpringBootTest (webEnvironment = RANDOM_PORT)
@ActiveProfiles ("test")
@DisplayName ("${Entity}Controller IT")
class ${Entity}ControllerIT {

    @LocalServerPort
    private int port;

    @MockitoBean
    private ${Entity}Service service;

    private static final String BASE = "/api/v1/$pluralName";

    private static final String VALID_JSON =
            "{\"name\":\"Test${Entity}\"}";

    private ${Entity}Response sampleResponse;

    @BeforeEach
    void setUp () {
        RestAssured.port = port;
        RestAssured.basePath = "";

        sampleResponse = new ${Entity}Response ($idLiteral, "Test${Entity}", Instant.now ());
    }

    // -------------------------------------------------------------------------
    @Nested
    @DisplayName ("POST /api/v1/$pluralName")
    class Create${Entity} {

        @Test
        @DisplayName ("201 - should create ${camelName} successfully")
        void create_Success () {
            when (service.create${Entity} (any ())).thenReturn (sampleResponse);

            given ().contentType (ContentType.JSON).body (VALID_JSON)
                    .when ().post (BASE)
                    .then ().statusCode (201)
                    .body ("name", equalTo ("Test${Entity}"));
        }

        @Test
        @DisplayName ("400 - should return 400 when request body is invalid")
        void create_InvalidBody () {
            given ().contentType (ContentType.JSON).body ("{}")
                    .when ().post (BASE)
                    .then ().statusCode (400);
        }

        @Test
        @DisplayName ("409 - should return 409 when ${camelName} already exists")
        void create_Duplicate () {
            when (service.create${Entity} (any ()))
                    .thenThrow (new DuplicateResourceException ("${Entity}", "name", "Test${Entity}"));

            given ().contentType (ContentType.JSON).body (VALID_JSON)
                    .when ().post (BASE)
                    .then ().statusCode (409);
        }
    }

    // -------------------------------------------------------------------------
    @Nested
    @DisplayName ("PUT /api/v1/$pluralName/{id}")
    class Update${Entity} {

        @Test
        @DisplayName ("200 - should update ${camelName} successfully")
        void update_Success () {
            when (service.update${Entity} ($idMatcher, any ())).thenReturn (sampleResponse);

            given ().contentType (ContentType.JSON).body (VALID_JSON)
                    .when ().put (BASE + "/$idPathLiteral")
                    .then ().statusCode (200)
                    .body ("name", equalTo ("Test${Entity}"));
        }

        @Test
        @DisplayName ("404 - should return 404 when ${camelName} not found")
        void update_NotFound () {
            when (service.update${Entity} ($idMatcher, any ()))
                    .thenThrow (new ResourceNotFoundException ("${Entity}", "id", $idLiteral));

            given ().contentType (ContentType.JSON).body (VALID_JSON)
                    .when ().put (BASE + "/$idPathLiteral")
                    .then ().statusCode (404);
        }
    }

    // -------------------------------------------------------------------------
    @Nested
    @DisplayName ("PATCH /api/v1/$pluralName/{id}")
    class Patch${Entity} {

        @Test
        @DisplayName ("200 - should patch ${camelName} successfully")
        void patch_Success () {
            when (service.patch${Entity} ($idMatcher, any ())).thenReturn (sampleResponse);

            given ().contentType (ContentType.JSON).body ("{\"name\":\"Updated${Entity}\"}")
                    .when ().patch (BASE + "/$idPathLiteral")
                    .then ().statusCode (200)
                    .body ("name", equalTo ("Test${Entity}"));
        }
    }

    // -------------------------------------------------------------------------
    @Nested
    @DisplayName ("GET /api/v1/$pluralName/{id}")
    class Get${Entity}ById {

        @Test
        @DisplayName ("200 - should return ${camelName} when found")
        void getById_Success () {
            when (service.get${Entity}ById ($idMatcher)).thenReturn (sampleResponse);

            given ()
                    .when ().get (BASE + "/$idPathLiteral")
                    .then ().statusCode (200)
                    .body ("id", equalTo ($idLiteral))
                    .body ("name", equalTo ("Test${Entity}"));
        }

        @Test
        @DisplayName ("404 - should return 404 when ${camelName} not found")
        void getById_NotFound () {
            when (service.get${Entity}ById ($idMatcher))
                    .thenThrow (new ResourceNotFoundException ("${Entity}", "id", $idLiteral));

            given ()
                    .when ().get (BASE + "/$idPathLiteral")
                    .then ().statusCode (404);
        }
    }

    // -------------------------------------------------------------------------
    @Nested
    @DisplayName ("GET /api/v1/$pluralName  (paginated)")
    class GetAll${Entity}s {

        @Test
        @DisplayName ("200 - should return paginated ${pluralName}")
        void getAll_Paginated () {
            when (service.getAll${Entity}s (any ())).thenReturn (
                    new PageImpl<> (List.of (sampleResponse)));

            given ()
                    .when ().get (BASE)
                    .then ().statusCode (200);
        }
    }

    // -------------------------------------------------------------------------
    @Nested
    @DisplayName ("GET /api/v1/$pluralName/all  (list)")
    class GetAll${Entity}sList {

        @Test
        @DisplayName ("200 - should return list of all ${pluralName}")
        void getAll_List () {
            when (service.getAll${Entity}s ()).thenReturn (List.of (sampleResponse));

            given ()
                    .when ().get (BASE + "/all")
                    .then ().statusCode (200)
                    .body ("`$", hasSize (1));
        }
    }

    // -------------------------------------------------------------------------
    @Nested
    @DisplayName ("DELETE /api/v1/$pluralName/{id}")
    class Delete${Entity} {

        @Test
        @DisplayName ("204 - should delete ${camelName} successfully")
        void delete_Success () {
            doNothing ().when (service).delete${Entity} ($idMatcher);

            given ()
                    .when ().delete (BASE + "/$idPathLiteral")
                    .then ().statusCode (204);
        }

        @Test
        @DisplayName ("404 - should return 404 when ${camelName} not found")
        void delete_NotFound () {
            doThrow (new ResourceNotFoundException ("${Entity}", "id", $idLiteral))
                    .when (service).delete${Entity} ($idMatcher);

            given ()
                    .when ().delete (BASE + "/$idPathLiteral")
                    .then ().statusCode (404);
        }
    }

    // -------------------------------------------------------------------------
    @Nested
    @DisplayName ("GET /api/v1/$pluralName/count")
    class Count${Entity}s {

        @Test
        @DisplayName ("200 - should return total count")
        void count_Success () {
            when (service.count${Entity}s ()).thenReturn (42L);

            given ()
                    .when ().get (BASE + "/count")
                    .then ().statusCode (200)
                    .body (equalTo ("42"));
        }
    }

    // -------------------------------------------------------------------------
    @Nested
    @DisplayName ("GET /api/v1/$pluralName/exists/{id}")
    class ExistsById {

        @Test
        @DisplayName ("200 true - should return true when ${camelName} exists")
        void exists_True () {
            when (service.existsById ($idMatcher)).thenReturn (true);

            given ()
                    .when ().get (BASE + "/exists/$idPathLiteral")
                    .then ().statusCode (200)
                    .body (equalTo ("true"));
        }

        @Test
        @DisplayName ("200 false - should return false when ${camelName} does not exist")
        void exists_False () {
            when (service.existsById ($idMatcher)).thenReturn (false);

            given ()
                    .when ().get (BASE + "/exists/$idPathLiteral")
                    .then ().statusCode (200)
                    .body (equalTo ("false"));
        }
    }
}
"@
Write-File -Path (Join-Path $JavaTest "controller\${Entity}ControllerIT.java") -Content $ControllerIT

# ==============================================================================
# Done
# ==============================================================================
Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  Files generated for: $Entity" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Files created (skipped if already existed):" -ForegroundColor Yellow
Write-Host "  dto\${Entity}Request.java"
Write-Host "  dto\${Entity}UpdateRequest.java"
Write-Host "  dto\${Entity}Response.java"
Write-Host "  mapper\${Entity}Mapper.java"
Write-Host "  repository\${Entity}Repository.java"
Write-Host "  service\${Entity}Service.java"
Write-Host "  service\${Entity}ServiceImpl.java"
Write-Host "  controller\${Entity}Controller.java"
Write-Host "  [TEST] service\${Entity}ServiceImplTest.java" -ForegroundColor Magenta
Write-Host "  [TEST] controller\${Entity}ControllerIT.java" -ForegroundColor Magenta
Write-Host ""
Write-Host "Still needed manually:" -ForegroundColor Cyan
Write-Host "  1. entity\${Entity}.java  -- add @Entity, @Column, FK fields, etc."
Write-Host "  2. Add @Mapping annotations in ${Entity}Mapper for FK fields"
Write-Host "  3. Fill in field mappings in ${Entity}ServiceImpl.updateEntity()"
Write-Host "  4. Add domain-specific query methods to ${Entity}Repository"
Write-Host "  5. Add FK validation in ServiceImpl (check parent entities exist)"
Write-Host "  6. Update CacheConfig.java with cache names: $pluralName, $camelName"
Write-Host "  7. Adjust ${Entity}ServiceImplTest if entity has FK dependencies"
Write-Host "  8. Adjust ${Entity}ControllerIT VALID_JSON to include all required request fields"
Write-Host ""
