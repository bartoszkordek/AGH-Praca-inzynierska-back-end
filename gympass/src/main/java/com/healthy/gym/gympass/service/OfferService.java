package com.healthy.gym.gympass.service;

import com.healthy.gym.gympass.dto.GymPassDTO;
import com.healthy.gym.gympass.exception.DuplicatedOffersException;
import com.healthy.gym.gympass.exception.GymPassNotFoundException;
import com.healthy.gym.gympass.exception.NoOffersException;
import com.healthy.gym.gympass.pojo.request.GymPassOfferRequest;

import java.util.List;

public interface OfferService {

    List<GymPassDTO> getGymPassOffers() throws NoOffersException;

    GymPassDTO createGymPassOffer(GymPassOfferRequest request) throws DuplicatedOffersException;

    GymPassDTO updateGymPassOffer(String id, GymPassOfferRequest request)
            throws DuplicatedOffersException, GymPassNotFoundException;

    GymPassDTO deleteGymPassOffer(String id) throws GymPassNotFoundException;
}
