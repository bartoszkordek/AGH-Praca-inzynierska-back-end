package com.healthy.gym.equipment.service;

import com.healthy.gym.equipment.component.ImageUrlCreator;
import com.healthy.gym.equipment.data.document.EquipmentDocument;
import com.healthy.gym.equipment.data.document.ImageDocument;
import com.healthy.gym.equipment.data.document.TrainingTypeDocument;
import com.healthy.gym.equipment.data.repository.EquipmentDAO;
import com.healthy.gym.equipment.data.repository.ImageDAO;
import com.healthy.gym.equipment.data.repository.TrainingTypeDAO;
import com.healthy.gym.equipment.dto.DescriptionDTO;
import com.healthy.gym.equipment.dto.EquipmentDTO;
import com.healthy.gym.equipment.dto.TrainingDTO;
import com.healthy.gym.equipment.exception.DuplicatedEquipmentTypeException;
import com.healthy.gym.equipment.model.request.EquipmentRequest;
import org.bson.types.Binary;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentDAO equipmentDAO;
    private final ImageDAO imageDAO;
    private final TrainingTypeDAO trainingTypeDAO;
    private final ImageUrlCreator imageUrlCreator;
    private final ModelMapper modelMapper;

    public EquipmentServiceImpl(
            EquipmentDAO equipmentDAO,
            ImageDAO imageDAO,
            TrainingTypeDAO trainingTypeDAO,
            ImageUrlCreator imageUrlCreator
    ){
        this.equipmentDAO = equipmentDAO;
        this.trainingTypeDAO = trainingTypeDAO;
        this.imageDAO = imageDAO;
        this.imageUrlCreator = imageUrlCreator;
        this.modelMapper = new ModelMapper();
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }


    @Override
    public List<EquipmentDTO> getEquipments() {
        List<EquipmentDocument> equipmentDocuments = equipmentDAO.findAll();

        return null;
    }

    @Override
    public EquipmentDTO createEquipment(EquipmentRequest equipmentRequest, MultipartFile multipartFile)
            throws DuplicatedEquipmentTypeException {

        String title = equipmentRequest.getTitle();
        if(equipmentDAO.existsByTitle(title)) throw new DuplicatedEquipmentTypeException();

        List<ImageDocument> imageDocuments = new ArrayList<>();
        List<String> imageUrls = new ArrayList<>();
        ImageDocument savedImageDocument = null;
        String imageUrl = null;
        String equipmentId = UUID.randomUUID().toString();
        if (multipartFile != null) {
            try {
                ImageDocument imageDocument = new ImageDocument(
                        UUID.randomUUID().toString(),
                        new Binary(multipartFile.getBytes()),
                        multipartFile.getContentType()
                );
                savedImageDocument = imageDAO.save(imageDocument);
                imageDocuments.add(savedImageDocument);
                imageUrl = imageUrlCreator.createImageUrl(imageDocument.getImageId());
                imageUrl += "?version=" + DigestUtils.md5DigestAsHex(multipartFile.getBytes());
                imageUrls.add(imageUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<String> trainingTypeIds = equipmentRequest.getTrainingIds();
        List<TrainingTypeDocument> trainingTypeDocuments = getTrainingTypeDocuments(trainingTypeIds);
        List<TrainingDTO> trainingDTOs = mapTrainingTypes(trainingTypeDocuments);
        String synopsis = equipmentRequest.getSynopsis();
        EquipmentDocument equipmentDocument = new EquipmentDocument(
                equipmentId,
                title,
                imageDocuments,
                imageUrls,
                synopsis,
                trainingTypeDocuments
        );

        var savedEquipment = equipmentDAO.save(equipmentDocument);
        EquipmentDTO equipmentDTO = modelMapper.map(savedEquipment, EquipmentDTO.class);

        DescriptionDTO descriptionDTO = new DescriptionDTO(
                savedEquipment.getSynopsis(),
                mapTrainingTypes(savedEquipment.getTrainings())
        );
        equipmentDTO.setDescription(descriptionDTO);

        return equipmentDTO;
    }

    private List<TrainingTypeDocument> getTrainingTypeDocuments(List<String> trainingTypeIds){
        List<TrainingTypeDocument> trainingTypeDocuments = new ArrayList<>();
        for(String trainingTypeId : trainingTypeIds){
            TrainingTypeDocument trainingTypeDocument = trainingTypeDAO.findByTrainingTypeId(trainingTypeId);
            trainingTypeDocuments.add(trainingTypeDocument);
        }
        return trainingTypeDocuments;
    }

    private List<TrainingDTO> mapTrainingTypes(List<TrainingTypeDocument> trainingTypeDocuments){
        List<TrainingDTO> trainingDTOs = new ArrayList<>();
        for(TrainingTypeDocument trainingTypeDocument : trainingTypeDocuments){
            TrainingDTO trainingDTO = new TrainingDTO();
            trainingDTO.setTrainingId(trainingTypeDocument.getTrainingTypeId());
            trainingDTO.setTitle(trainingTypeDocument.getName());
            trainingDTOs.add(trainingDTO);
        }
        return trainingDTOs;
    }
}
