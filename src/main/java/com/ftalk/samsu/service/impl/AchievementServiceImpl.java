package com.ftalk.samsu.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftalk.samsu.event.AssigneeCertificateCreateEvent;
import com.ftalk.samsu.event.ParticipantCertificateCreateEvent;
import com.ftalk.samsu.exception.BadRequestException;
import com.ftalk.samsu.exception.UnauthorizedException;
import com.ftalk.samsu.model.achievement.Achievement;
import com.ftalk.samsu.model.achievement.AchievementTemplate;
import com.ftalk.samsu.model.role.RoleName;
import com.ftalk.samsu.model.semester.Semester;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.achievement.*;
import com.ftalk.samsu.repository.*;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.*;
import com.ftalk.samsu.utils.AppUtils;
import com.ftalk.samsu.utils.ListConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AchievementServiceImpl implements AchievementService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AchievementServiceImpl.class);

    @Autowired
    private SemesterService semesterService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AchievementRepository achievementRepository;
    @Autowired
    private AchievementTemplateRepository achievementTemplateRepository;

    @Override
    public PagedResponse<AchievementResponse> getAllAchievementResponse(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        Page<Achievement> achievements = achievementRepository.findAll(pageable);

        List<Achievement> content = achievements.getNumberOfElements() == 0 ? Collections.emptyList() : achievements.getContent();

        if (achievements.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), achievements.getNumber(), achievements.getSize(),
                    achievements.getTotalElements(), achievements.getTotalPages(), achievements.isLast());
        }
        return new PagedResponse<>(ListConverter.listToList(content, AchievementResponse::new), achievements.getNumber(),
                achievements.getSize(), achievements.getTotalElements(), achievements.getTotalPages(), achievements.isLast());
    }

    @Override
    public List<AchievementResponse> getAchievementResponseByRollnumberAndSemester(String rollnumber, String semester) {
        User user = userRepository.getUserByRollnumber(rollnumber);
        List<Achievement> achievements = achievementRepository.findAllByOwnerIdAndSemester(user.getId(), semester);
        return ListConverter.listToList(achievements,AchievementResponse::new);
    }

    @Override
    public PagedResponse<AchievementTemplateResponse> getAllAchievementTemplateResponse(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id");

        Page<AchievementTemplate> achievements = achievementTemplateRepository.findAll(pageable);

        List<AchievementTemplate> content = achievements.getNumberOfElements() == 0 ? Collections.emptyList() : achievements.getContent();

        if (achievements.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), achievements.getNumber(), achievements.getSize(),
                    achievements.getTotalElements(), achievements.getTotalPages(), achievements.isLast());
        }
        return new PagedResponse<>(ListConverter.listToList(content, AchievementTemplateResponse::new), achievements.getNumber(),
                achievements.getSize(), achievements.getTotalElements(), achievements.getTotalPages(), achievements.isLast());
    }


    @Override
    public AchievementResponse getAchievementResponse(Integer id, UserPrincipal currentUser) {
        Achievement achievement = achievementRepository.findById(id).orElseThrow(() -> new BadRequestException("Achievement not found with id " + id));
        return new AchievementResponse(achievement);
    }

    @Override
    public AchievementTemplateResponse getAchievementTemplateResponse(Integer id, UserPrincipal currentUser) {
        AchievementTemplate achievementTemplate = achievementTemplateRepository.findById(id).orElseThrow(() -> new BadRequestException("AchievementTemplate not found with id " + id));
        return new AchievementTemplateResponse(achievementTemplate);
    }

    @Transactional
    @Override
    public AchievementResponse updateAchievementResponse(Integer id, AchievementRequest achievementRequest, UserPrincipal currentUser) {
        User creator = userRepository.getUserByRollnumber(achievementRequest.getOwnerRollnumber());
        Semester semester = semesterService.getSemester(achievementRequest.getSemesterName());
        Achievement achievement = achievementRepository.findById(id).orElseThrow(() -> new BadRequestException("Achievement not found with id " + id));
        boolean isAdminOrManager = currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_MANAGER.toString()));
        if (isAdminOrManager) {
            achievement.setTitle(achievementRequest.getTitle());
            achievement.setContent(achievementRequest.getContent());
            achievement.setOwner(creator);
            achievement.setSemester(semester);
            if (achievementRequest.getAchievementTemplateId() != null) {
                AchievementTemplate achievementTemplate = achievementTemplateRepository.findById(achievementRequest.getAchievementTemplateId())
                        .orElseThrow(() -> new BadRequestException("AchievementTemplate not found with id " + id));
                achievement.setAchievementTemplate(achievementTemplate);
            }
            achievement.setUrl(achievementRequest.getUrl());
        }
        return new AchievementResponse(achievementRepository.save(achievement));
    }

    @Transactional
    @Override
    public AchievementTemplateResponse updateAchievementTemplateResponse(Integer id, AchievementTemplateRequest achievementTemplateRequest,
                                                                         UserPrincipal currentUser) {
        User user = userRepository.getUser(currentUser);
        AchievementTemplate achievementTemplate = achievementTemplateRepository.findById(id).orElseThrow(() -> new BadRequestException("AchievementTemplate not found with id " + id));
        boolean isAdminOrManager = currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_MANAGER.toString()));
        if (isAdminOrManager) {
            achievementTemplate.setTitle(achievementTemplateRequest.getTitle());
            achievementTemplate.setContent(achievementTemplateRequest.getContent());
        }
        return new AchievementTemplateResponse(achievementTemplateRepository.save(achievementTemplate));
    }

    @Override
    public PagedResponse<AchievementResponse> getAllAchievementResponseBySemester(String semester, Integer page, Integer size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        Page<Achievement> achievements = achievementRepository.findAchievementBySemesterName(semester, pageable);

        List<Achievement> content = achievements.getNumberOfElements() == 0 ? Collections.emptyList() : achievements.getContent();

        if (achievements.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), achievements.getNumber(), achievements.getSize(),
                    achievements.getTotalElements(), achievements.getTotalPages(), achievements.isLast());
        }
        return new PagedResponse<>(ListConverter.listToList(content, AchievementResponse::new), achievements.getNumber(),
                achievements.getSize(), achievements.getTotalElements(), achievements.getTotalPages(), achievements.isLast());
    }


    @Transactional
    @Override
    public AchievementResponse createAchievementResponse(AchievementRequest achievementRequest, UserPrincipal currentUser) {
        User user = userRepository.getUser(currentUser);
        User owner = userRepository.getUserByRollnumber(achievementRequest.getOwnerRollnumber());
        Semester semester = semesterService.getSemester(achievementRequest.getSemesterName());
        if (!currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))
                && !currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_MANAGER.toString()))) {
            throw new UnauthorizedException("You don't have permission");
        }
        Achievement achievement = new Achievement(achievementRequest.getTitle(), achievementRequest.getContent(), achievementRequest.getUrl());
        achievement.setSemester(semester);
        achievement.setOwner(owner);
        achievement.setCreator(user);
        if (achievementRequest.getAchievementTemplateId() != null) {
            AchievementTemplate achievementTemplate = achievementTemplateRepository.findById(achievementRequest.getAchievementTemplateId())
                    .orElseThrow(() -> new BadRequestException("AchievementTemplate not found with id " + achievementRequest.getAchievementTemplateId()));
            achievement.setAchievementTemplate(achievementTemplate);
        }

        return new AchievementResponse(achievementRepository.save(achievement));
    }

    @Transactional
    @Override
    public AchievementTemplateResponse createAchievementTemplateResponse(AchievementTemplateRequest achievementTemplateRequest, UserPrincipal currentUser) {
        if (!currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))
                && !currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_MANAGER.toString()))) {
            throw new UnauthorizedException("You don't have permission");
        }
        AchievementTemplate achievement = new AchievementTemplate(achievementTemplateRequest.getTitle(), achievementTemplateRequest.getContent());
        return new AchievementTemplateResponse(achievementTemplateRepository.save(achievement));
    }

    @EventListener
    @Transactional
    public void handleParticipantCertificateCreateEvent(ParticipantCertificateCreateEvent achievementEvent) {
        try {
            ParticipantAchievementCreateRequest achievement = new ParticipantAchievementCreateRequest(achievementEvent.getEventTitle(), achievementEvent.getSemesterName(), achievementEvent.getEventDate(), achievementEvent.getParticipants());
            RestTemplate restTemplate = new RestTemplate();
            String resourceUrl = "https://upload.samsu-fpt.software/generate-participant-certificates";
            ResponseEntity<List> response = restTemplate.postForEntity(resourceUrl, new HttpEntity<ParticipantAchievementCreateRequest>(achievement), List.class);
            System.out.println(response.getBody());
            if (response.getBody() == null) return;
            ObjectMapper mapper = new ObjectMapper();
            List<CertificateResponse> achievementResponses = new ArrayList<>();
            for (Object object : response.getBody()) {
                CertificateResponse certificateResponse = mapper.convertValue(object, CertificateResponse.class);
                achievementResponses.add(certificateResponse);
            }

            System.out.println(achievementResponses);
            String achievementTitle = "Achievement for " + achievement.getEventTitle();
            String achievementContent = "Achievement for " + achievement.getEventTitle() + " on " + achievement.getEventDate();
            List<Achievement> achievements = new ArrayList<>();
                for (CertificateResponse certificateResponse : achievementResponses) {
                    User user = userRepository.getUserByRollnumber(certificateResponse.getRollnumber());
                    User creatorUser = userRepository.getUserByRollnumber(achievementEvent.getCurrentUser().getRollnumber());
                    Semester semester = semesterService.getSemester(achievement.getSemesterName());
                    Achievement achievement1 = new Achievement(achievementTitle, achievementContent, certificateResponse.getCertificateUrl());
                    achievement1.setSemester(semester);
                    achievement1.setOwner(user);
                    achievement1.setCreator(creatorUser);
//                    achievement1.setAchievementTemplate(achievementTemplateRepository.findById(achievementCreateResponse.getAchievementTemplateId())
//                            .orElseThrow(() -> new BadRequestException("AchievementTemplate not found with id " + achievementCreateResponse.getAchievementTemplateId())));
                    achievements.add(achievement1);
                }
            achievementRepository.saveAll(achievements);
        } catch (Exception e) {
            LOGGER.error("Error while create achievement", e);
        }


    }

    @EventListener
    @Transactional
    public void handleAssignmentCertificateCreateEvent(AssigneeCertificateCreateEvent achievementEvent) {
        try {
            AssigneeCertificateCreateRequest achievement = new AssigneeCertificateCreateRequest(achievementEvent.getEventTitle(), achievementEvent.getSemesterName(), achievementEvent.getEventDate(), achievementEvent.getTaskList());
            RestTemplate restTemplate = new RestTemplate();
            String resourceUrl = "https://upload.samsu-fpt.software/generate-achievement-certificates";
            ResponseEntity<List> response = restTemplate.postForEntity(resourceUrl, new HttpEntity<AssigneeCertificateCreateRequest>(achievement), List.class);
            if (response.getBody() == null) return;
            ObjectMapper mapper = new ObjectMapper();
            List<CertificateResponse> achievementResponses = new ArrayList<>();
            for (Object object : response.getBody()) {
                CertificateResponse certificateResponse = mapper.convertValue(object, CertificateResponse.class);
                achievementResponses.add(certificateResponse);
            }

            System.out.println(achievementResponses);
            String achievementTitle = "Achievement for " + achievement.getEventTitle();
            String achievementContent = "Achievement for " + achievement.getEventTitle() + " on " + achievement.getEventDate();
            List<Achievement> achievements = new ArrayList<>();
            for (CertificateResponse certificateResponse : achievementResponses) {
                User user = userRepository.getUserByRollnumber(certificateResponse.getRollnumber());
                User creatorUser = userRepository.getUserByRollnumber(achievementEvent.getCurrentUser().getRollnumber());
                Semester semester = semesterService.getSemester(achievement.getSemesterName());
                Achievement achievement1 = new Achievement(achievementTitle, achievementContent, certificateResponse.getCertificateUrl());
                achievement1.setSemester(semester);
                achievement1.setOwner(user);
                achievement1.setCreator(creatorUser);
//                    achievement1.setAchievementTemplate(achievementTemplateRepository.findById(achievementCreateResponse.getAchievementTemplateId())
//                            .orElseThrow(() -> new BadRequestException("AchievementTemplate not found with id " + achievementCreateResponse.getAchievementTemplateId())));
                achievements.add(achievement1);
            }
            achievementRepository.saveAll(achievements);
        } catch (Exception e) {
            LOGGER.error("Error while create achievement", e);
        }
    }
}