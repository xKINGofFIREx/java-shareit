package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getItem(long itemId, long sharerId) {
        return get("/" + itemId, sharerId);
    }

    public ResponseEntity<Object> addItem(ItemDto itemDto, long sharerId) {
        return post("", sharerId, itemDto);
    }

    public void deleteItem(long itemId) {
        delete("/" + itemId);
    }

    public ResponseEntity<Object> patchItem(long itemId, ItemDto itemDto, long sharerId) {
        return patch("/" + itemId, sharerId, itemDto);
    }

    public ResponseEntity<Object> findAll(long sharerId, Integer from, Integer size) {
        if (from == null || size == null)
            return get("", sharerId);

        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );

        return get("?from={from}&size={size}", sharerId, parameters);
    }

    public ResponseEntity<Object> getItemByText(long sharerId, String text, Integer from, Integer size) {
        if (from == null || size == null)
            return get("/search?text={text}", sharerId, Map.of("text", text));

        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );

        return get("/search?text={text}&from={from}&size={size}", sharerId, parameters);
    }


    public ResponseEntity<Object> createComment(long itemId, CommentDto commentDto, long sharerId) {
        return post("/" + itemId + "/comment", sharerId, commentDto);
    }

}
