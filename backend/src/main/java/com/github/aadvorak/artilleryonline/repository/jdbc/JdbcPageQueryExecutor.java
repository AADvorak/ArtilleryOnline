package com.github.aadvorak.artilleryonline.repository.jdbc;

import com.github.aadvorak.artilleryonline.repository.utils.SqlUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class JdbcPageQueryExecutor<T> {

    private final String baseQuery;
    private final Map<String, String> conditions;
    private final RowMapper<T> rowMapper;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Page<T> execute(Map<String, Object> parameters, Pageable pageable) {
        var queryTemplate = makeQueryTemplate(parameters);
        var resultQuery = makeResultQuery(queryTemplate, pageable);
        List<T> result = jdbcTemplate.query(resultQuery, parameters, rowMapper);
        if (result.isEmpty()) {
            return Page.empty(pageable);
        }
        if (pageable.getPageNumber() == 0 && pageable.getPageSize() > result.size()) {
            return new PageImpl<>(result);
        }
        var countQuery = makeCountQuery(queryTemplate);
        var total = jdbcTemplate.queryForObject(countQuery, parameters, Long.class);
        return new PageImpl<>(result, pageable, total != null ? total : 0L);
    }

    public String makeQueryTemplate(Map<String, Object> parameters) {
        return baseQuery + SqlUtils.makeConditionsQuery(parameters, conditions);
    }

    private String makeResultQuery(String queryTemplate, Pageable pageable) {
        return queryTemplate.replace("{}", "*")
                + makeOrderByQuery(pageable) + makeLimitQuery(pageable);
    }

    private String makeCountQuery(String queryTemplate) {
        return queryTemplate.replace("{}", "count(1)");
    }

    private String makeOrderByQuery(Pageable pageable) {
        var order = pageable.getSort().stream().findFirst().orElseThrow();
        return " order by " + camelToSnake(order.getProperty()) + " " + order.getDirection();
    }

    private String makeLimitQuery(Pageable pageable) {
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        String limit = " fetch first " + size + " rows only";
        if (page > 0) {
            return limit + " offset " + page * size;
        }
        return limit;
    }

    private String camelToSnake(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        char c = str.charAt(0);
        result.append(Character.toLowerCase(c));
        for (int i = 1; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                result.append('_');
                result.append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }
}
