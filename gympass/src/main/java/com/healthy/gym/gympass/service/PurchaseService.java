package com.healthy.gym.gympass.service;

import com.healthy.gym.gympass.dto.PurchasedGymPassDTO;
import com.healthy.gym.gympass.pojo.request.PurchasedGymPassRequest;

public interface PurchaseService {

    PurchasedGymPassDTO purchaseGymPass(PurchasedGymPassRequest request);

}
