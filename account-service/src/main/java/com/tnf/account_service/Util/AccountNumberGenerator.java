package com.tnf.account_service.Util;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.tnf.account_service.Entity.AccountSequence;

import lombok.RequiredArgsConstructor;

// Account numbers come from an atomic Mongo counter, so they stay unique across
// multiple service instances without exposing the Mongo _id. Format: AC000001.
@Component
@RequiredArgsConstructor
public class AccountNumberGenerator {

    private static final String PREFIX = "AC";
    private static final String SEQUENCE_ID = "account_number";
    private static final int SEQUENCE_WIDTH = 6;

    private final MongoTemplate mongoTemplate;

    public String generateAccountNumber() {
        Query query = new Query(Criteria.where("_id").is(SEQUENCE_ID));
        Update update = new Update().inc("sequence", 1);
        // returnNew reads back the incremented value; upsert seeds the counter on first use.
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true).upsert(true);

        AccountSequence sequence = mongoTemplate.findAndModify(query, update, options, AccountSequence.class);
        long nextSequence = sequence != null && sequence.getSequence() != null ? sequence.getSequence() : 1L;

        return PREFIX + String.format("%0" + SEQUENCE_WIDTH + "d", nextSequence);
    }
}
