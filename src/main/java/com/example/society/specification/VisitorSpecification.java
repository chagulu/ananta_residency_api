package com.example.society.specification;

import com.example.society.guest.entity.Visitor;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VisitorSpecification {

    public static Specification<Visitor> getVisitorFilters(Map<String, String> filters) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.containsKey("id")) {
                predicates.add(cb.equal(root.get("id"), Long.parseLong(filters.get("id"))));
            }
            if (filters.containsKey("guestName")) {
                predicates.add(cb.like(cb.lower(root.get("guestName")), "%" + filters.get("guestName").toLowerCase() + "%"));
            }
            if (filters.containsKey("mobile")) {
                // changed to LIKE for partial search
                predicates.add(cb.like(root.get("mobile"), "%" + filters.get("mobile") + "%"));
            }
            if (filters.containsKey("approveStatus")) {
                predicates.add(cb.equal(root.get("approveStatus"),
                        Visitor.ApproveStatus.valueOf(filters.get("approveStatus"))));
            }

            // restrict by resident’s flat & building
            if (filters.containsKey("flatNumber")) {
                predicates.add(cb.equal(root.get("flatNumber"), filters.get("flatNumber")));
            }
            if (filters.containsKey("buildingNumber")) {
                predicates.add(cb.equal(root.get("buildingNumber"), filters.get("buildingNumber")));
            }

            // ✅ Date range filter
            if (filters.containsKey("fromDate") && filters.containsKey("toDate")) {
                LocalDateTime from = LocalDateTime.parse(filters.get("fromDate"));
                LocalDateTime to = LocalDateTime.parse(filters.get("toDate"));
                predicates.add(cb.between(root.get("visitTime"), from, to));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
