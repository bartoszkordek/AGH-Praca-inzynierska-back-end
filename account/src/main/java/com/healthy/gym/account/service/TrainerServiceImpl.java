package com.healthy.gym.account.service;

import com.healthy.gym.account.component.ImageUrlCreator;
import com.healthy.gym.account.data.document.ImageDocument;
import com.healthy.gym.account.data.document.TrainerDocument;
import com.healthy.gym.account.data.document.TrainingTypeDocument;
import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.data.repository.ImageDAO;
import com.healthy.gym.account.data.repository.TrainerDAO;
import com.healthy.gym.account.data.repository.TrainingTypeDAO;
import com.healthy.gym.account.data.repository.UserDAO;
import com.healthy.gym.account.dto.TrainerDTO;
import com.healthy.gym.account.exception.NoUserFound;
import com.healthy.gym.account.pojo.request.TrainerRequest;
import org.bson.types.Binary;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.healthy.gym.account.util.TrainerMapper.mapTrainerDocumentToTrainerDTO;
import static com.healthy.gym.account.util.TrainerMapper.mapTrainerDocumentsToTrainerDTOs;

@Service
public class TrainerServiceImpl implements TrainerService{

    private final UserDAO userDAO;
    private final TrainerDAO trainerDAO;
    private final TrainingTypeDAO trainingTypeDAO;
    private final ImageDAO imageDAO;
    private final ImageUrlCreator imageUrlCreator;
    private final ModelMapper modelMapper;

    public TrainerServiceImpl(
            UserDAO userDAO,
            TrainerDAO trainerDAO,
            TrainingTypeDAO trainingTypeDAO,
            ImageDAO imageDAO,
            ImageUrlCreator imageUrlCreator
    ){
        this.userDAO = userDAO;
        this.trainerDAO = trainerDAO;
        this.trainingTypeDAO = trainingTypeDAO;
        this.imageDAO = imageDAO;
        this.imageUrlCreator = imageUrlCreator;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public TrainerDTO createTrainer(String userId, TrainerRequest trainerRequest, MultipartFile multipartFile)
            throws NoUserFound {

        UserDocument userDocument = userDAO.findByUserId(userId);
        if(userDocument == null) throw new NoUserFound();

        List<ImageDocument> imageDocuments = new ArrayList<>();
        List<String> imageUrls = new ArrayList<>();
        ImageDocument savedImageDocument = null;
        String imageUrl = null;
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
        String synopsis = trainerRequest.getSynopsis();
        String full = trainerRequest.getFull();
        List<String> trainingTypeIds = trainerRequest.getTrainingIds();
        List<TrainingTypeDocument> trainingTypeDocuments = getTrainingTypeDocuments(trainingTypeIds);
        TrainerDocument trainerDocument = new TrainerDocument(
                userDocument,
                imageDocuments,
                imageUrls,
                synopsis,
                full,
                trainingTypeDocuments
        );
        var savedTrainer = trainerDAO.save(trainerDocument);
        return mapTrainerDocumentToTrainerDTO(savedTrainer);
    }

    @Override
    public List<TrainerDTO> getTrainers() throws NoUserFound {
        List<TrainerDocument> trainerDocuments = trainerDAO.findAll();
        if (trainerDocuments.isEmpty()) throw new NoUserFound();
        return mapTrainerDocumentsToTrainerDTOs(trainerDocuments);
    }

    private List<TrainingTypeDocument> getTrainingTypeDocuments(List<String> trainingTypeIds) {
        List<TrainingTypeDocument> trainingTypeDocuments = new ArrayList<>();
        for (String trainingTypeId : trainingTypeIds) {
            TrainingTypeDocument trainingTypeDocument = trainingTypeDAO.findByTrainingTypeId(trainingTypeId);
            trainingTypeDocuments.add(trainingTypeDocument);
        }
        return trainingTypeDocuments;
    }
}