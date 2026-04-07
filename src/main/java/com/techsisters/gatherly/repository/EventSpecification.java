package com.techsisters.gatherly.repository;

import java.time.OffsetDateTime;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.techsisters.gatherly.dto.EventDTO;
import com.techsisters.gatherly.dto.EventDTO.ListType;
import com.techsisters.gatherly.entity.Event;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class EventSpecification {

    public static Specification<Event> filter(ListType listType, String searchQuery) {
        return new Specification<Event>() {

            @Override
            public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();

                if (StringUtils.containsIgnoreCase(listType.toString(), EventDTO.ListType.UPCOMING.toString())) {

                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.greaterThanOrEqualTo(root.get("eventDateTime"), OffsetDateTime.now()));

                }

                if (StringUtils.containsIgnoreCase(listType.toString(), EventDTO.ListType.PAST.toString())) {

                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.lessThan(root.get("eventDateTime"), OffsetDateTime.now()));

                }

                if (StringUtils.isNotBlank(searchQuery)) {
                    String likePattern = "%" + searchQuery.toLowerCase() + "%";

                    Predicate titleMatch = criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("title")), likePattern);

                    Predicate hostNameMatch = criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("eventHostName")), likePattern);

                    Predicate tagsMatch = criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("tags")), likePattern);

                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.or(titleMatch, hostNameMatch, tagsMatch));

                }
                return predicate;

            }

        };

    }

}
