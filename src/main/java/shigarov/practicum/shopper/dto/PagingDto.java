package shigarov.practicum.shopper.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PagingDto {
    private int pageNumber;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;

    public int pageNumber() {
        return pageNumber;
    }

    public int pageSize() {
        return pageNumber;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public boolean hasPrevious() {
        return hasPrevious;
    }

    public static PagingDto of(@NonNull Page<?> page) {
        int pageNumber = page.getNumber();
        int pageSize = page.getSize();
        boolean hasNext = page.hasNext();
        boolean hasPrevious = page.hasPrevious();

        return new PagingDto(pageNumber, pageSize, hasNext, hasPrevious);
    }
}
