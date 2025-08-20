package com.example.customer_management_app.dto;

// Creo este envoltorio genérico para devolver resultados paginados con metadatos
// que al frontend le sirven para construir tablas (paginador, total, etc.).

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PageResponse", description = "Response for paginated results")
public class PageResponse<T> {

    @Schema(description = "Elements of the current page")
    private List<T> content;

    @Schema(description = "Page number (0-based)", example = "0")
    private int page;

    @Schema(description = "Page size", example = "20")
    private int size;

    @Schema(description = "Total number of elements", example = "123")
    private long totalElements;

    @Schema(description = "Total number of pages", example = "7")
    private int totalPages;

    @Schema(description = "Is this the first page?", example = "true")
    private boolean first;

    @Schema(description = "Is this the last page?", example = "false")
    private boolean last;

    public PageResponse() {}

    public PageResponse(List<T> content, int page, int size, long totalElements, int totalPages, boolean first, boolean last) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
    }

    // Getters y setters
    public List<T> getContent() { return content; }
    public void setContent(List<T> content) { this.content = content; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public boolean isFirst() { return first; }
    public void setFirst(boolean first) { this.first = first; }

    public boolean isLast() { return last; }
    public void setLast(boolean last) { this.last = last; }
}
