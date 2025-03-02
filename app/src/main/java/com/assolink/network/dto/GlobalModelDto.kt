package com.assolink.network.dto

import com.assolink.network.dto.association.AssociationDto

data class GlobalModelDto(
    val associations: List<AssociationDto>,
)
