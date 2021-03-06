package com.healthy.gym.gympass.service;

import com.healthy.gym.gympass.data.document.GymPassDocument;
import com.healthy.gym.gympass.data.document.PurchasedGymPassDocument;
import com.healthy.gym.gympass.data.document.UserDocument;
import com.healthy.gym.gympass.data.repository.GymPassOfferDAO;
import com.healthy.gym.gympass.data.repository.PurchasedGymPassDAO;
import com.healthy.gym.gympass.data.repository.UserDAO;
import com.healthy.gym.gympass.dto.PurchasedGymPassDTO;
import com.healthy.gym.gympass.dto.PurchasedGymPassStatusValidationResultDTO;
import com.healthy.gym.gympass.dto.PurchasedUserGymPassDTO;
import com.healthy.gym.gympass.exception.*;
import com.healthy.gym.gympass.pojo.request.PurchasedGymPassRequest;
import com.healthy.gym.gympass.util.RequestDateFormatter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private static final String MIN_START_DATE = "1000-01-01";
    private static final String MAX_END_DATE = "9999-12-31";
    private static final String USER_NOT_EXIST_MESSAGE = "User not exist";
    private static final String START_DATE_AFTER_END_DATE_MESSAGE = "Start date after end date";
    private static final String GYMPASS_NOT_EXIST_MESSAGE = "Gympass with current ID does not exist";
    private static final String NO_GYMPASSES_CONTENT_MESSAGE = "No gympasses to display";
    private static final String RETRO_SUSPENSION_DATE_MESSAGE = "Retro suspension date";
    private static final String SUSPENSION_DATE_AFTER_END_DATE_MESSAGE = "Suspension date after end date";
    private static final String ALREADY_SUSPENDED_GYMPASS_MESSAGE = "Gympass is already suspended";

    private final PurchasedGymPassDAO purchasedGymPassDAO;
    private final GymPassOfferDAO gymPassOfferDAO;
    private final UserDAO userDAO;
    private final ModelMapper modelMapper;
    private final RequestDateFormatter requestDateFormatter;

    @Autowired
    public PurchaseServiceImpl(
            PurchasedGymPassDAO purchasedGymPassDAO,
            GymPassOfferDAO gymPassOfferDAO,
            UserDAO userDAO
    ) {
        this.purchasedGymPassDAO = purchasedGymPassDAO;
        this.gymPassOfferDAO = gymPassOfferDAO;
        this.userDAO = userDAO;
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        requestDateFormatter = new RequestDateFormatter();
    }

    @Override
    public PurchasedGymPassDTO purchaseGymPass(PurchasedGymPassRequest request)
            throws OfferNotFoundException,
            UserNotFoundException,
            PastDateException {

        GymPassDocument gymPassOfferDocument = getGymPassDocument(request);
        UserDocument userDocument = getClientUserDocument(request);
        LocalDate validityStartDate = getValidityStartDate(request);
        LocalDate validityEndDate = getValidityEndDate(validityStartDate, gymPassOfferDocument);
        LocalDateTime purchasedDateTime = LocalDateTime.now();
        int entries = getMaxEntriesForPurchasedGymPass(gymPassOfferDocument);

        PurchasedGymPassDocument purchasedGymPassDocumentToSave = new PurchasedGymPassDocument(
                UUID.randomUUID().toString(),
                gymPassOfferDocument,
                userDocument,
                purchasedDateTime,
                validityStartDate,
                validityEndDate,
                entries
        );
        PurchasedGymPassDocument purchasedGymPassDocumentSaved = purchasedGymPassDAO
                .save(purchasedGymPassDocumentToSave);
        return modelMapper.map(purchasedGymPassDocumentSaved, PurchasedGymPassDTO.class);
    }

    private GymPassDocument getGymPassDocument(PurchasedGymPassRequest request) throws OfferNotFoundException {
        String gymPassOfferId = request.getGymPassOfferId();
        GymPassDocument gymPassOfferDocument = gymPassOfferDAO.findByDocumentId(gymPassOfferId);
        if (gymPassOfferDocument == null) throw new OfferNotFoundException("Gympass offer not exist");
        return gymPassOfferDocument;
    }

    private UserDocument getClientUserDocument(PurchasedGymPassRequest request) throws UserNotFoundException {
        String userId = request.getUserId();
        UserDocument userDocument = userDAO.findByUserId(userId);
        if (userDocument == null) throw new UserNotFoundException(USER_NOT_EXIST_MESSAGE);
        return userDocument;
    }

    private LocalDate getValidityStartDate(PurchasedGymPassRequest request) throws PastDateException {
        String startDate = request.getStartDate();
        LocalDate validityStartDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);
        if (validityStartDate.isBefore(LocalDate.now())) throw new PastDateException();
        return validityStartDate;
    }

    private int getMaxEntriesForPurchasedGymPass(GymPassDocument gymPassDocument) {
        boolean isTemporaryGymPass = gymPassDocument.isTemporaryPass();
        if (isTemporaryGymPass) return Integer.MAX_VALUE;
        return gymPassDocument.getQuantity();
    }

    private LocalDate getValidityEndDate(LocalDate validityStartDate, GymPassDocument gymPassDocument) {
        boolean isTemporaryGymPass = gymPassDocument.isTemporaryPass();
        if (isTemporaryGymPass) {
            int gymPassValidityInDays = gymPassDocument.getQuantity();
            return validityStartDate.plusDays(gymPassValidityInDays);
        }
        //spring framework bug
        //return LocalDate.MAX;
        return validityStartDate.plusYears(100);
    }

    @Override
    public PurchasedGymPassDTO suspendGymPass(String individualGymPassId, String requestedSuspensionDate)
            throws GymPassNotFoundException, AlreadySuspendedGymPassException, RetroSuspensionDateException,
            SuspensionDateAfterEndDateException {

        PurchasedGymPassDocument purchasedGymPassDocument = purchasedGymPassDAO
                .findByPurchasedGymPassDocumentId(individualGymPassId);
        if (purchasedGymPassDocument == null) throw new GymPassNotFoundException(GYMPASS_NOT_EXIST_MESSAGE);

        LocalDate endDate = purchasedGymPassDocument.getEndDate();
        LocalDate now = LocalDate.now();
        LocalDate suspensionDate = LocalDate.parse(requestedSuspensionDate, DateTimeFormatter.ISO_DATE);
        if (suspensionDate.isBefore(now) || suspensionDate.isEqual(now))
            throw new RetroSuspensionDateException(RETRO_SUSPENSION_DATE_MESSAGE);
        if (suspensionDate.isAfter(endDate) || suspensionDate.isEqual(endDate))
            throw new SuspensionDateAfterEndDateException(SUSPENSION_DATE_AFTER_END_DATE_MESSAGE);

        LocalDate currentSuspensionDate = purchasedGymPassDocument.getSuspensionDate();
        if (currentSuspensionDate != null && currentSuspensionDate.isAfter(suspensionDate))
            throw new AlreadySuspendedGymPassException(ALREADY_SUSPENDED_GYMPASS_MESSAGE);

        purchasedGymPassDocument.setSuspensionDate(suspensionDate);
        long suspensionDateFromNow = now.until(suspensionDate, ChronoUnit.DAYS);
        purchasedGymPassDocument.setEndDate(endDate.plusDays(suspensionDateFromNow));
        PurchasedGymPassDocument purchasedGymPassDocumentSaved = purchasedGymPassDAO.save(purchasedGymPassDocument);
        return modelMapper.map(purchasedGymPassDocumentSaved, PurchasedGymPassDTO.class);
    }

    @Override
    public PurchasedGymPassStatusValidationResultDTO checkGymPassValidityStatus(String individualGymPassId)
            throws GymPassNotFoundException {

        PurchasedGymPassDocument purchasedGymPassDocument = purchasedGymPassDAO
                .findByPurchasedGymPassDocumentId(individualGymPassId);
        if (purchasedGymPassDocument == null) throw new GymPassNotFoundException(GYMPASS_NOT_EXIST_MESSAGE);

        LocalDate now = LocalDate.now();
        LocalDate endDate = purchasedGymPassDocument.getEndDate();
        LocalDate suspensionDate = purchasedGymPassDocument.getSuspensionDate();
        int entries = purchasedGymPassDocument.getEntries();

        boolean valid = true;
        String suspensionDateResponse = null;

        if (now.isAfter(endDate) || entries < 1) valid = false;
        if (suspensionDate != null) {
            suspensionDateResponse = suspensionDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            if (now.isBefore(suspensionDate)) valid = false;
        }

        return new PurchasedGymPassStatusValidationResultDTO(
                valid,
                endDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                entries,
                suspensionDateResponse
        );

    }

    @Override
    public List<PurchasedGymPassDTO> getGymPasses(
            String requestPurchaseStartDate,
            String requestPurchaseEndDate,
            Pageable pageable
    ) throws StartDateAfterEndDateException, NoGymPassesException {

        LocalDateTime purchaseStartDateTime = LocalDateTime.now().minusMonths(1);
        LocalDateTime purchaseEndDateTime = LocalDateTime.now();

        if (requestPurchaseStartDate != null) {
            LocalDate purchaseStartDateParsed = LocalDate.parse(requestPurchaseStartDate, DateTimeFormatter.ISO_LOCAL_DATE);
            purchaseStartDateTime = purchaseStartDateParsed.atTime(23, 59, 59);
        }

        if (requestPurchaseEndDate != null) {
            LocalDate purchaseEndDateParsed = LocalDate.parse(requestPurchaseEndDate, DateTimeFormatter.ISO_LOCAL_DATE);
            purchaseEndDateTime = purchaseEndDateParsed.atTime(0, 0, 0);
        }

        if (purchaseStartDateTime.isAfter(purchaseEndDateTime))
            throw new StartDateAfterEndDateException(START_DATE_AFTER_END_DATE_MESSAGE);

        List<PurchasedGymPassDocument> purchasedGymPassDocuments = purchasedGymPassDAO
                .findAllByPurchaseDateTimeBetween(
                        purchaseStartDateTime.minusDays(1),
                        purchaseEndDateTime.plusDays(1),
                        pageable
                ).getContent();

        if (purchasedGymPassDocuments.isEmpty()) throw new NoGymPassesException(NO_GYMPASSES_CONTENT_MESSAGE);

        return purchasedGymPassDocuments
                .stream()
                .map(purchasedGymPassDocument -> modelMapper.map(purchasedGymPassDocument, PurchasedGymPassDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchasedUserGymPassDTO> getUserGymPasses(
            String userId,
            String requestStartDate,
            String requestEndDate
    ) throws UserNotFoundException, StartDateAfterEndDateException, NoGymPassesException {

        String startDate = MIN_START_DATE;
        String endDate = MAX_END_DATE;
        if (requestStartDate != null) startDate = requestStartDate;

        if (requestEndDate != null) endDate = requestEndDate;

        LocalDate formattedStartDate = requestDateFormatter.formatStartDate(startDate);
        LocalDate formattedEndDate = requestDateFormatter.formatEndDate(endDate);

        if (formattedStartDate.isAfter(formattedEndDate))
            throw new StartDateAfterEndDateException(START_DATE_AFTER_END_DATE_MESSAGE);

        UserDocument userDocument = userDAO.findByUserId(userId);
        if (userDocument == null) throw new UserNotFoundException(USER_NOT_EXIST_MESSAGE);

        List<PurchasedGymPassDocument> purchasedGymPassDocuments = purchasedGymPassDAO
                .findAllByUserAndStartDateAfterAndEndDateBefore(userDocument, formattedStartDate, formattedEndDate);

        if (purchasedGymPassDocuments.isEmpty()) throw new NoGymPassesException(NO_GYMPASSES_CONTENT_MESSAGE);

        return purchasedGymPassDocuments
                .stream()
                .map(purchasedGymPassDocument -> modelMapper.map(purchasedGymPassDocument, PurchasedUserGymPassDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public PurchasedUserGymPassDTO getUserLatestGympass(String userId)
            throws UserNotFoundException, NoGymPassesException {

        UserDocument userDocument = userDAO.findByUserId(userId);
        if (userDocument == null) throw new UserNotFoundException(USER_NOT_EXIST_MESSAGE);

        List<PurchasedGymPassDocument> purchasedGymPassDocuments = purchasedGymPassDAO.findAllByUserAndEndDateAfter(
                userDocument,
                LocalDate.now()
        );

        if (purchasedGymPassDocuments.isEmpty()) throw new NoGymPassesException();

        Optional<PurchasedGymPassDocument> latestGympassDocumentOptional = purchasedGymPassDocuments
                .stream()
                .sorted(Comparator.nullsLast((d1, d2) -> d1.getEndDate().compareTo(d2.getEndDate())))
                .findFirst();

        PurchasedGymPassDocument latestGympassDocument = latestGympassDocumentOptional.get();

        return modelMapper.map(latestGympassDocument, PurchasedUserGymPassDTO.class);
    }

    @Override
    public PurchasedGymPassDTO deleteGymPass(String individualGymPassId) throws GymPassNotFoundException {

        PurchasedGymPassDocument purchasedGymPassDocumentToRemove = purchasedGymPassDAO
                .findByPurchasedGymPassDocumentId(individualGymPassId);
        if (purchasedGymPassDocumentToRemove == null) throw new GymPassNotFoundException(GYMPASS_NOT_EXIST_MESSAGE);

        purchasedGymPassDAO.delete(purchasedGymPassDocumentToRemove);

        return modelMapper.map(purchasedGymPassDocumentToRemove, PurchasedGymPassDTO.class);
    }
}
