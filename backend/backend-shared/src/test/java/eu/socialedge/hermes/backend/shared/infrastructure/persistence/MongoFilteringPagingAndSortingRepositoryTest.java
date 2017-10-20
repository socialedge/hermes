/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2017 SocialEdge
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package eu.socialedge.hermes.backend.shared.infrastructure.persistence;

import eu.socialedge.hermes.backend.shared.domain.Filter;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataMongoTest
public class MongoFilteringPagingAndSortingRepositoryTest {

    private static final List<TestDocument> BASE_DOCUMENTS = asList(
        new TestDocument("assemble"),
        new TestDocument("bootRepackage"),
        new TestDocument("build"),
        new TestDocument("buildDependents"),
        new TestDocument("buildNeeded")
    );

    private static final List<TestDocument> BUILD_DOCUMENTS = BASE_DOCUMENTS.stream()
        .filter(d -> d.getName().equals("build")).collect(Collectors.toList());

    private static final List<TestDocument> BUILD_LIKE_DOCUMENTS = BASE_DOCUMENTS.stream()
        .filter(d -> d.getName().contains("build")).collect(Collectors.toList());

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TestFilteringPagingAndSortingRepository repository;

    @Before
    public void reloadTestDocumentCollection() {
        mongoTemplate.remove(new Query(), TestDocument.class);
        BASE_DOCUMENTS.forEach(mongoTemplate::insert);
    }

    @Test
    public void findsAllDocumentsWithNameEqBuild() {
        val buildDocuments = repository.findAll("name", "build");

        assertEquals(BUILD_DOCUMENTS, buildDocuments);
    }

    @Test
    public void findsAllDocumentsWithNameLikeBuild() {
        val buildDocuments = repository.findAll(Filter.from("name", "build"));

        assertEquals(BUILD_LIKE_DOCUMENTS, buildDocuments);
    }
}
