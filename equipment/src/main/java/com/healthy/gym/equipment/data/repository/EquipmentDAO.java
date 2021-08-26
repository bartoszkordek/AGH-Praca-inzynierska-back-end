package com.healthy.gym.equipment.data.repository;

import com.healthy.gym.equipment.data.document.EquipmentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EquipmentDAO extends MongoRepository<EquipmentDocument, String> {
}
