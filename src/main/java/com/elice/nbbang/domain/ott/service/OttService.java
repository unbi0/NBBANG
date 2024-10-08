package com.elice.nbbang.domain.ott.service;

import com.elice.nbbang.domain.ott.controller.dto.OttCreateRequest;
import com.elice.nbbang.domain.ott.controller.dto.OttResponse;
import com.elice.nbbang.domain.ott.controller.dto.OttUpdateRequest;
import com.elice.nbbang.domain.ott.entity.Ott;
import com.elice.nbbang.domain.ott.exception.DuplicateOttName;
import com.elice.nbbang.domain.ott.exception.InvalidOttCapacity;
import com.elice.nbbang.domain.ott.exception.OttNotFoundException;
import com.elice.nbbang.domain.ott.repository.OttRepository;
import com.elice.nbbang.domain.ott.service.dto.OttCreateServiceRequest;
import com.elice.nbbang.domain.ott.service.dto.OttUpdateServiceRequest;
import com.elice.nbbang.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OttService {
    private static final int MIN_CAPACITY = 1;
    private static final int MAX_CAPACITY = 5;

    private final OttRepository ottRepository;


    public Long createOtt(final OttCreateServiceRequest request) {
        validateOttName(request.name());
        validateOttCapacity(request.capacity());

        final Ott ott = Ott.of(request.name(), request.price(), request.capacity());
        ottRepository.save(ott);

        return ott.getId();
    }

    @Transactional(readOnly = true)
    public List<OttResponse> getAllOtt() {
        final List<Ott> results = ottRepository.findAll();

        return results.stream()
                .map(ott -> new OttResponse(ott.getId(), ott.getName(), ott.getPrice(), ott.getCapacity()))
                .toList();
    }

    @Transactional(readOnly = true)
    public OttResponse getOttById(final Long ottId) {
        final Ott ott = ottRepository.findById(ottId)
                .orElseThrow(() -> new OttNotFoundException(ErrorCode.NOT_FOUND_OTT));

        return new OttResponse(ott.getId(), ott.getName(), ott.getPrice(), ott.getCapacity());
    }

    public void updateOtt(final Long ottId, final OttUpdateServiceRequest request) {
        final Ott ott = ottRepository.findById(ottId)
                .orElseThrow(() -> new OttNotFoundException(ErrorCode.NOT_FOUND_OTT));

        ott.updateOtt(request.name(), request.price(), request.capacity());

        ottRepository.save(ott);
    }

    /*
    * 외래키 제약 조건 때문에 삭제가 안되는 에러는 어떻게 처리를 해야하나?
    * */
    public void deleteOtt(final Long ottId) {
        final Ott ott = ottRepository.findById(ottId)
                .orElseThrow(() -> new OttNotFoundException(ErrorCode.NOT_FOUND_OTT));
        ottRepository.delete(ott);
    }


    private void validateOttName(String name) {
        if (ottRepository.existsByName(name)) {
            throw new DuplicateOttName(ErrorCode.DUPLICATE_OTT_NAME);
        }
    }

    private void validateOttCapacity(int capacity) {
        if (!(MIN_CAPACITY < capacity && capacity <= MAX_CAPACITY)) {
            throw new InvalidOttCapacity(ErrorCode.INVALID_CAPACITY);
        }
    }
}
