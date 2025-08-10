package com.example.customer_management_app.dto;

// Creo este envoltorio genérico para devolver resultados paginados con metadatos
// que al frontend le sirven para construir tablas (paginador, total, etc.).

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PageResponse", description = "Respuesta paginada con contenido y metadatos")
public class PageResponse<T> {

    @Schema(description = "Elementos de la página actual")
    private List<T> content;

    @Schema(description = "Número de página (base 0)", example = "0")
    private int page;

    @Schema(description = "Tamaño de página", example = "20")
    private int size;

    @Schema(description = "Cantidad total de elementos", example = "123")
    private long totalElements;

    @Schema(description = "Cantidad total de páginas", example = "7")
    private int totalPages;

    @Schema(description = "¿Esta es la primera página?", example = "true")
    private boolean first;

    @Schema(description = "¿Esta es la última página?", example = "false")
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
