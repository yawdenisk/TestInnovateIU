package org.example;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

    private HashMap<String, Document> storage = new HashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */

    public Document save(Document document){
        if(document.getId() == null || document.getId().isEmpty()){
            document = Document.builder()
                    .id(UUID.randomUUID().toString())
                    .title(document.getTitle())
                    .content(document.getContent())
                    .author(document.getAuthor())
                    .created(Optional.ofNullable(document.getCreated()).orElse(Instant.now()))
                    .build();
        }
        storage.put(document.getId(), document);
        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */

    public List<Document> search(SearchRequest request) {
        return storage.values().stream()
                .filter(document -> matchesTitlePrefixes(document, request))
                .filter(document -> matchesContent(document, request))
                .filter(document -> matchesAuthor(document, request))
                .filter(document -> matchesCreationFrom(document, request))
                .filter(document -> matchesCreationTo(document, request))
                .collect(Collectors.toList());
    }

    private boolean matchesTitlePrefixes(Document document, SearchRequest request) {
        if (request.getTitlePrefixes() == null) return true;
        return request.getTitlePrefixes().stream()
                .anyMatch(title -> document.getTitle().startsWith(title));
    }

    private boolean matchesContent(Document document, SearchRequest request) {
        if (request.getContainsContents() == null) return true;
        return request.getContainsContents().stream()
                .anyMatch(content -> document.getContent().contains(content));
    }

    private boolean matchesAuthor(Document document, SearchRequest request) {
        if (request.getAuthorIds() == null) return true;
        return request.getAuthorIds().stream()
                .anyMatch(id -> document.getAuthor().getId().equals(id));
    }
    private boolean matchesCreationFrom(Document document, SearchRequest request) {
        if(request.getCreatedFrom() == null) return true;
        return request.getCreatedFrom().isBefore(document.getCreated());
    }

    private boolean matchesCreationTo(Document document, SearchRequest request) {
        if(request.getCreatedTo() == null) return true;
        return request.getCreatedTo().isAfter(document.getCreated());
    }

    /**
     * Implementation this method should find document by id
     * @param id - document id
     * @return optional document
     */

    public Optional<Document> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}