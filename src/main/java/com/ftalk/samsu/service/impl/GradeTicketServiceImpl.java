package com.ftalk.samsu.service.impl;

import com.ftalk.samsu.exception.BadRequestException;
import com.ftalk.samsu.exception.ResourceNotFoundException;
import com.ftalk.samsu.exception.UnauthorizedException;
import com.ftalk.samsu.model.gradePolicy.GradeSubCriteria;
import com.ftalk.samsu.model.gradePolicy.GradeTicket;
import com.ftalk.samsu.model.role.RoleName;
import com.ftalk.samsu.model.semester.Semester;
import com.ftalk.samsu.model.user.User;
import com.ftalk.samsu.payload.ApiResponse;
import com.ftalk.samsu.payload.PagedResponse;
import com.ftalk.samsu.payload.gradePolicy.GradeTicketCreateRequest;
import com.ftalk.samsu.payload.gradePolicy.GradeTicketUpdateRequest;
import com.ftalk.samsu.payload.gradePolicy.GradeTicketResponse;
import com.ftalk.samsu.repository.GradeSubCriteriaRepository;
import com.ftalk.samsu.repository.GradeTicketRepository;
import com.ftalk.samsu.repository.SemesterRepository;
import com.ftalk.samsu.repository.UserRepository;
import com.ftalk.samsu.security.UserPrincipal;
import com.ftalk.samsu.service.GradeTicketService;
import com.ftalk.samsu.utils.AppUtils;
import com.ftalk.samsu.utils.ListConverter;
import com.ftalk.samsu.utils.grade.GradeTicketConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@Service
public class GradeTicketServiceImpl implements GradeTicketService {
    @Autowired
    private GradeTicketRepository gradeTicketRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SemesterRepository semesterRepository;
    @Autowired
    private GradeSubCriteriaRepository gradeSubCriteriaRepository;

    @Override
    public PagedResponse<GradeTicketResponse> getAllGradeTickets(int page, int size) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        Page<GradeTicket> gradeTickets = gradeTicketRepository.findAll(pageable);

        List<GradeTicket> content = gradeTickets.getNumberOfElements() == 0 ? Collections.emptyList() : gradeTickets.getContent();

        return getGradeTicketResponse(gradeTickets);
    }

    @Override
    public PagedResponse<GradeTicketResponse> getGradeTicketsByGradeSubCriteriaId(int page, int size, Integer gradeSubCriteriaId) {
        AppUtils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        Page<GradeTicket> gradeTickets = gradeTicketRepository.findByGradeSubCriteriaId(gradeSubCriteriaId, pageable);

        List<GradeTicket> content = gradeTickets.getNumberOfElements() == 0 ? Collections.emptyList() : gradeTickets.getContent();

        return getGradeTicketResponse(gradeTickets);
    }

    @Override
    public GradeTicketResponse getGradeTicket(Integer id) {
        GradeTicket ticket = gradeTicketRepository.findById(id).orElseThrow(() -> new BadRequestException("GradeTicket not found with id " + id));
        return new GradeTicketResponse(ticket);
    }

    @Transactional
    @Override
    public GradeTicketResponse updateGradeTicket(Integer id, GradeTicketUpdateRequest gradeTicketRequest, UserPrincipal currentUser) {
        User user = userRepository.getUser(currentUser);
        User guarantor = userRepository.findByRollnumber(gradeTicketRequest.getGuarantorRollnumber()).orElseThrow(() -> new ResourceNotFoundException("User", "Guarantor", gradeTicketRequest.getGuarantorRollnumber()));
        Semester semester = semesterRepository.findById(gradeTicketRequest.getSemesterName()).orElseThrow(() -> new ResourceNotFoundException("Semester", "name", gradeTicketRequest.getSemesterName()));
        GradeTicket gradeTicket = gradeTicketRepository.findById(id).orElseThrow(() -> new BadRequestException("GradeTicket not found with id " + id));
        boolean isAdminOrManager = currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())) || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_MANAGER.toString()));
        if (gradeTicketRequest.getTitle() != null)
            gradeTicket.setTitle(gradeTicketRequest.getTitle());
        if (gradeTicketRequest.getSemesterName() != null)
            gradeTicket.setSemester(semester);
        if (gradeTicketRequest.getGuarantorRollnumber() != null) {
            if (!isAdminOrManager && gradeTicket.getGuarantor() != null && guarantor != gradeTicket.getGuarantor()) {
                gradeTicket.setStatus(GradeTicketConstants.PROCESSING.getValue());
            }
            gradeTicket.setGuarantor(guarantor);
        }
        if (gradeTicketRequest.getContent() != null)
            gradeTicket.setContent(gradeTicketRequest.getContent());
        if (gradeTicketRequest.getEvidenceUrls() != null)
            gradeTicket.setEvidenceUrls(gradeTicketRequest.getEvidenceUrls());
        if (gradeTicketRequest.getFeedback() != null)
            gradeTicket.setFeedback(gradeTicketRequest.getFeedback());
        if (gradeTicketRequest.getGradeSubCriteriaId() != null) {
            GradeSubCriteria gradeSubCriteria = gradeSubCriteriaRepository.findById(gradeTicketRequest.getGradeSubCriteriaId()).orElseThrow(() -> new BadRequestException("GradeSubCriteria not found with id " + gradeTicketRequest.getGradeSubCriteriaId()));
            gradeTicket.setGradeSubCriteria(gradeSubCriteria);
        }
        if (isAdminOrManager) {
            if (gradeTicketRequest.getStatus() != null) {
                boolean isChangeStatus = gradeTicket.getStatus() == null || gradeTicket.getStatus() != gradeTicketRequest.getStatus();
                boolean isApproved = isChangeStatus && gradeTicketRequest.getStatus() == GradeTicketConstants.APPROVED.getValue();
                boolean isUnApprove = isChangeStatus && gradeTicket.getStatus() == GradeTicketConstants.APPROVED.getValue();
                boolean isIntactApproved = gradeTicket.getStatus() == gradeTicketRequest.getStatus()
                        && gradeTicket.getStatus() == GradeTicketConstants.APPROVED.getValue();

                if (isApproved) {
                    gradeTicket.setAccepterUser(user);
                    if (gradeTicketRequest.getScore() != null) {
                        User creator = gradeTicket.getCreatorUser();
                        creator.setScore((short) (creator.getScore() + gradeTicketRequest.getScore()));
                        userRepository.save(creator);
                    }
                } else if (isUnApprove) {
                    gradeTicket.setAccepterUser(null);
                    if (gradeTicket.getScore() != null) {
                        User creator = gradeTicket.getCreatorUser();
                        creator.setScore((short) (creator.getScore() - gradeTicket.getScore()));
                        userRepository.save(creator);
                    }
                } else if (isIntactApproved && gradeTicket.getScore() != gradeTicketRequest.getScore()) {
                    User creator = gradeTicket.getCreatorUser();
                    creator.setScore((short) (creator.getScore() - gradeTicket.getScore() + gradeTicketRequest.getScore()));
                    userRepository.save(creator);
                }
                gradeTicket.setStatus(gradeTicketRequest.getStatus());
                gradeTicket.setScore(gradeTicketRequest.getScore());

            }
        } else {
            if (gradeTicketRequest.getStatus() != null) {
                ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to change status");
                throw new UnauthorizedException(apiResponse);
            }
            gradeTicket.setStatus(GradeTicketConstants.PROCESSING.getValue());
            gradeTicket.setAccepterUser(null);
        }
        GradeTicket savedGradeTicket = gradeTicketRepository.save(gradeTicket);
        return new GradeTicketResponse(savedGradeTicket);
    }

    @Transactional
    @Override
    public GradeTicketResponse updateGradeTicketV2(Integer id, GradeTicketUpdateRequest gradeTicketRequest, UserPrincipal currentUser) {
        User user = userRepository.getUser(currentUser);
        User guarantor = userRepository.findByRollnumber(gradeTicketRequest.getGuarantorRollnumber()).orElseThrow(() -> new ResourceNotFoundException("User", "Guarantor", gradeTicketRequest.getGuarantorRollnumber()));
        Semester semester = semesterRepository.findById(gradeTicketRequest.getSemesterName()).orElseThrow(() -> new ResourceNotFoundException("Semester", "name", gradeTicketRequest.getSemesterName()));
        GradeTicket gradeTicket = gradeTicketRepository.findById(id).orElseThrow(() -> new BadRequestException("GradeTicket not found with id " + id));
        boolean isAdminOrManager = currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())) || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_MANAGER.toString()));
        if (gradeTicketRequest.getStatus() != null) {
            if (isAdminOrManager) {
                boolean isChangeStatus = gradeTicket.getStatus() == null || gradeTicket.getStatus() != gradeTicketRequest.getStatus();
                boolean isApproved = isChangeStatus && gradeTicketRequest.getStatus() == GradeTicketConstants.APPROVED.getValue();
                boolean isUnApprove = isChangeStatus && gradeTicket.getStatus() == GradeTicketConstants.APPROVED.getValue();
                boolean isIntactApproved = gradeTicket.getStatus() == gradeTicketRequest.getStatus()
                        && gradeTicket.getStatus() == GradeTicketConstants.APPROVED.getValue();

                if (isApproved) {
                    gradeTicket.setAccepterUser(user);
                    if (gradeTicketRequest.getScore() != null) {
                        User creator = gradeTicket.getCreatorUser();
                        creator.setScore((short) (creator.getScore() + gradeTicketRequest.getScore()));
                        userRepository.save(creator);
                    }
                } else if (isUnApprove) {
                    gradeTicket.setAccepterUser(null);
                    if (gradeTicket.getScore() != null) {
                        User creator = gradeTicket.getCreatorUser();
                        creator.setScore((short) (creator.getScore() - gradeTicket.getScore()));
                        userRepository.save(creator);
                    }
                } else if (isIntactApproved && gradeTicket.getScore() != gradeTicketRequest.getScore()) {
                    User creator = gradeTicket.getCreatorUser();
                    creator.setScore((short) (creator.getScore() - gradeTicket.getScore() + gradeTicketRequest.getScore()));
                    userRepository.save(creator);
                }
                gradeTicket.setStatus(gradeTicketRequest.getStatus());
                gradeTicket.setScore(gradeTicketRequest.getScore());
                if (gradeTicketRequest.getStatus() == GradeTicketConstants.APPROVED.getValue() || gradeTicketRequest.getStatus() == GradeTicketConstants.REJECTED.getValue()) {
                    gradeTicket.setAccepterUser(user);
                } else {
                    gradeTicket.setAccepterUser(null);
                }
                if (gradeTicketRequest.getFeedback() != null)
                    gradeTicket.setFeedback(gradeTicketRequest.getFeedback());
                if (gradeTicketRequest.getGradeSubCriteriaId() != null) {
                    GradeSubCriteria gradeSubCriteria = gradeSubCriteriaRepository.findById(gradeTicketRequest.getGradeSubCriteriaId()).orElseThrow(() -> new BadRequestException("GradeSubCriteria not found with id " + gradeTicketRequest.getGradeSubCriteriaId()));
                    gradeTicket.setGradeSubCriteria(gradeSubCriteria);
                }
                GradeTicket savedGradeTicket = gradeTicketRepository.save(gradeTicket);
                return new GradeTicketResponse(savedGradeTicket);
            } else {
                ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to change status");
                throw new UnauthorizedException(apiResponse);
            }
        }
        if (gradeTicketRequest.getTitle() != null)
            gradeTicket.setTitle(gradeTicketRequest.getTitle());
        if (gradeTicketRequest.getContent() != null)
            gradeTicket.setContent(gradeTicketRequest.getContent());
        if (gradeTicketRequest.getEvidenceUrls() != null)
            gradeTicket.setEvidenceUrls(gradeTicketRequest.getEvidenceUrls());
        if (gradeTicketRequest.getSemesterName() != null)
            gradeTicket.setSemester(semester);
        if (gradeTicketRequest.getGuarantorRollnumber() != null) {
            if (!isAdminOrManager && gradeTicket.getGuarantor() != null && guarantor != gradeTicket.getGuarantor()) {
                gradeTicket.setStatus(GradeTicketConstants.PROCESSING.getValue());
            }
            gradeTicket.setGuarantor(guarantor);
        }
        gradeTicket.setStatus(GradeTicketConstants.PROCESSING.getValue());
        gradeTicket.setAccepterUser(null);
        GradeTicket savedGradeTicket = gradeTicketRepository.save(gradeTicket);
        return new GradeTicketResponse(savedGradeTicket);
    }

    @Override
    @Transactional
    public GradeTicketResponse addGradeTicket(GradeTicketCreateRequest gradeTicketRequest, UserPrincipal currentUser) {
        User creator = userRepository.getUser(currentUser);
        User guarantor = userRepository.findByRollnumber(gradeTicketRequest.getGuarantorRollnumber()).orElseThrow(() -> new ResourceNotFoundException("User", "Guarantor", gradeTicketRequest.getGuarantorRollnumber()));
        Semester semester = semesterRepository.findById(gradeTicketRequest.getSemesterName()).orElseThrow(() -> new ResourceNotFoundException("Semester", "name", gradeTicketRequest.getSemesterName()));
        GradeTicket gradeTicket = new GradeTicket(gradeTicketRequest.getTitle(), gradeTicketRequest.getContent(), gradeTicketRequest.getEvidenceUrls(), gradeTicketRequest.getFeedback(), creator);
        gradeTicket.setStatus(GradeTicketConstants.PROCESSING.getValue());
        gradeTicket.setGuarantor(guarantor);
        gradeTicket.setSemester(semester);
        GradeTicket savedGradeTicket = gradeTicketRepository.save(gradeTicket);
        return new GradeTicketResponse(savedGradeTicket);
    }

    @Override
    public ApiResponse deleteGradeTicket(Integer id, UserPrincipal currentUser) {
        GradeTicket gradeTicket = gradeTicketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Semester", "id", id));
        if (currentUser.getAuthorities()
                .contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
            gradeTicketRepository.deleteById(id);
            return new ApiResponse(Boolean.TRUE, "You successfully deleted gradeTicket");
        }
        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to delete this gradeTicket");
        throw new UnauthorizedException(apiResponse);
    }

    @Override
    public List<GradeTicket> finAllGradeTicketApproved(String semester, Integer uid){
        return gradeTicketRepository.findAllByCreatorUser_IdAndSemester_NameAndStatus(uid,semester,GradeTicketConstants.APPROVED.getValue());
    }

    private PagedResponse<GradeTicketResponse> getGradeTicketResponse(Page<GradeTicket> gradeTickets) {
        if (gradeTickets.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), gradeTickets.getNumber(), gradeTickets.getSize(), gradeTickets.getTotalElements(), gradeTickets.getTotalPages(), gradeTickets.isLast());
        }
        return new PagedResponse<>(ListConverter.listToList(gradeTickets.getContent(), GradeTicketResponse::new), gradeTickets.getNumber(), gradeTickets.getSize(), gradeTickets.getTotalElements(), gradeTickets.getTotalPages(), gradeTickets.isLast());
    }
}