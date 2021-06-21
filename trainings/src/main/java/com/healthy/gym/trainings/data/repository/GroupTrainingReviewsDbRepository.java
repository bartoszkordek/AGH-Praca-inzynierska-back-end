package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.data.document.GroupTrainingsReviews;
import com.healthy.gym.trainings.model.request.GroupTrainingReviewRequest;
import com.healthy.gym.trainings.model.request.GroupTrainingReviewUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GroupTrainingReviewsDbRepository {

    @Autowired
    private Environment environment;

    @Autowired
    private final ReviewDAO reviewDAO;

    public GroupTrainingReviewsDbRepository(ReviewDAO reviewDAO){
        this.reviewDAO = reviewDAO;
    }

    public List<GroupTrainingsReviews> getGroupTrainingReviews(){
        return reviewDAO.findAll();
    }

    public GroupTrainingsReviews getGroupTrainingsReviewById(String reviewId){
        return reviewDAO.findGroupTrainingsReviewsById(reviewId);
    }

    public boolean isGroupTrainingsReviewExist(String reviewId){
        return reviewDAO.existsById(reviewId);
    }

    public boolean isClientReviewOwner(String reviewId, String clientId){
        return reviewDAO.existsByIdAndAndClientId(reviewId, clientId);
    }

//    public GroupTrainingsReviews createGroupTrainingReview(GroupTrainingReviewRequest groupTrainingsReviewsModel,
//                                                           String date,
//                                                           String clientId){
//
//        GroupTrainingsReviews response = reviewDAO.insert(new GroupTrainingsReviews(
//                groupTrainingsReviewsModel.getTrainingName(),
//                clientId,
//                date,
//                groupTrainingsReviewsModel.getStars(),
//                groupTrainingsReviewsModel.getText()
//        ));
//        return response;
//    }

    public GroupTrainingsReviews removeGroupTrainingsReview(String reviewId){
        GroupTrainingsReviews groupTrainingsReviews = reviewDAO.findGroupTrainingsReviewsById(reviewId);
        reviewDAO.removeById(reviewId);
        return groupTrainingsReviews;
    }

    public GroupTrainingsReviews updateGroupTrainingsReview(GroupTrainingReviewUpdateRequest groupTrainingsReviewsUpdateModel,
                                                            String reviewId){
        GroupTrainingsReviews groupTrainingsReview = reviewDAO.findGroupTrainingsReviewsById(reviewId);
        int stars = groupTrainingsReviewsUpdateModel.getStars();
        String text = groupTrainingsReviewsUpdateModel.getText();
        if(stars >= 1 && stars <=5){
            groupTrainingsReview.setStars(groupTrainingsReviewsUpdateModel.getStars());
        }
        if(!text.isEmpty()){
            groupTrainingsReview.setText(groupTrainingsReviewsUpdateModel.getText());
        }
        GroupTrainingsReviews response = reviewDAO.save(groupTrainingsReview);
        return response;
    }
}
