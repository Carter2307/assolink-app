package com.assolink.network.mapper

import com.assolink.data.model.association.Association
import com.assolink.network.dto.association.AssociationDto

fun mapAssociationDtoToAssociationModel(dto: AssociationDto): Association {
    return Association(
        dto.id,
        dto.name,
        dto.description,
        dto.address,
        dto.latitude,
        dto.longitude,
        dto.logoUrl,
        dto.email,
        dto.phone
    )
}