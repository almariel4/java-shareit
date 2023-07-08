package ru.practicum.shareit;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exceptions.BadRequestException;

public class PageRequestUtil {

    public static PageRequest createPageRequest(Integer from, Integer size) {
        PageRequest pageRequest = null;
        if (from != null || size != null) {
            if (from < 0 || size < 0) {
                throw new BadRequestException("Индекс первого элемента и количество элементов не могут быть отрицательными");
            }
            if (from == 0 && size == 0) {
                throw new BadRequestException("Нечего возвращать");
            }
            int pageNumber = from / size;
            pageRequest = PageRequest.of(pageNumber, Math.toIntExact(size));
        }
        return pageRequest;
    }
}
