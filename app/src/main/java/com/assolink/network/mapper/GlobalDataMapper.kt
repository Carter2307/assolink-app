package com.assolink.network.mapper

import com.assolink.data.model.GlobalDataModel
import com.assolink.network.dto.GlobalModelDto

fun mapGlobalDataDtoToGlobalDataModel(dto: GlobalModelDto): GlobalDataModel {
    return GlobalDataModel(
        associations = dto.associations.map { mapAssociationDtoToAssociationModel(it) }
    )
}