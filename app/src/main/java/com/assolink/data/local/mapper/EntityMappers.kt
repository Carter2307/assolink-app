package com.assolink.data.local.mapper

import com.assolink.data.local.entities.AssociationEntity
import com.assolink.data.local.entities.EventEntity
import com.assolink.data.model.Association
import com.assolink.data.model.Event
import com.google.firebase.firestore.GeoPoint
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object EntityMappers {

    private val gson = Gson()

    fun mapAssociationToEntity(association: Association): AssociationEntity {
        return AssociationEntity(
            id = association.id,
            name = association.name,
            description = association.description,
            category = association.category,
            address = association.address,
            locationLat = association.location?.latitude,
            locationLng = association.location?.longitude,
            contactEmail = association.contactEmail,
            contactPhone = association.contactPhone,
            website = association.website,
            imageUrl = association.imageUrl,
            eventsJson = gson.toJson(association.events),
            membersJson = gson.toJson(association.members),
            createdAt = association.createdAt,
            updatedAt = association.updatedAt
        )
    }

    fun mapEntityToAssociation(entity: AssociationEntity): Association {
        val location = if (entity.locationLat != null && entity.locationLng != null) {
            GeoPoint(entity.locationLat, entity.locationLng)
        } else null

        val eventsType = object : TypeToken<List<String>>() {}.type
        val membersType = object : TypeToken<List<String>>() {}.type

        return Association(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            category = entity.category,
            address = entity.address,
            location = location,
            contactEmail = entity.contactEmail,
            contactPhone = entity.contactPhone,
            website = entity.website,
            imageUrl = entity.imageUrl,
            events = gson.fromJson(entity.eventsJson, eventsType) ?: emptyList(),
            members = gson.fromJson(entity.membersJson, membersType) ?: emptyList(),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun mapEventToEntity(event: Event): EventEntity {
        return EventEntity(
            id = event.id,
            associationId = event.associationId,
            title = event.title,
            description = event.description,
            startDate = event.startDate,
            endDate = event.endDate,
            locationLat = event.location?.latitude,
            locationLng = event.location?.longitude,
            address = event.address,
            imageUrl = event.imageUrl,
            maxParticipants = event.maxParticipants,
            participantsJson = gson.toJson(event.participants),
            createdAt = event.createdAt,
            updatedAt = event.updatedAt
        )
    }

    fun mapEntityToEvent(entity: EventEntity): Event {
        val location = if (entity.locationLat != null && entity.locationLng != null) {
            GeoPoint(entity.locationLat, entity.locationLng)
        } else null

        val participantsType = object : TypeToken<List<String>>() {}.type

        return Event(
            id = entity.id,
            associationId = entity.associationId,
            title = entity.title,
            description = entity.description,
            startDate = entity.startDate,
            endDate = entity.endDate,
            location = location,
            address = entity.address,
            imageUrl = entity.imageUrl,
            maxParticipants = entity.maxParticipants,
            participants = gson.fromJson(entity.participantsJson, participantsType) ?: emptyList(),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}