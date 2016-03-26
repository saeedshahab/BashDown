package com.saeedshahab.bashdown.repositories;

import com.google.common.base.Optional;
import com.saeedshahab.bashdown.models.Bash;
import com.saeedshahab.bashdown.wrappers.DatabaseWrapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class BashRepositoryTest {

    private BashRepository bashRepository;

    private Bash bash;

    @Mock
    private DatabaseWrapper databaseWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        bashRepository = new BashRepository(databaseWrapper, Jackson.newObjectMapper());

        bash = new Bash();
    }

    @Test
    public void testCreatePass() throws Exception {
        String title = "Bash";
        String description = "description";
        Date dateTime = new Date();
        String image = "imageURL";
        String imageTitle = "imageTitle";
        String imageDescription = "imageDescription";

        bash.setTitle(title);
        bash.setDescription(description);
        bash.setDateTime(dateTime);
        bash.setId(null);
        bash.setActive(Boolean.FALSE);
        bash.setImage(image);
        bash.setImageTitle(imageTitle);
        bash.setImageDescription(imageDescription);

        when(databaseWrapper.create(bash, Bash.class)).thenReturn(bash);

        Optional<Bash> bashOptional = bashRepository.create(bash);

        assertThat(bashOptional.isPresent()).isTrue();

        Bash bashReturn = bashOptional.get();
        assertThat(bashReturn.getTitle()).isEqualTo(title);
        assertThat(bashReturn.getDescription()).isEqualTo(description);
        assertThat(bashReturn.getDateTime()).isEqualTo(dateTime);
        assertThat(bashReturn.getId()).isNotNull();
        assertThat(bashReturn.getActive()).isTrue();
        assertThat(bashReturn.getImage()).isEqualTo(image);
        assertThat(bashReturn.getImageTitle()).isEqualTo(imageTitle);
        assertThat(bashReturn.getImageDescription()).isEqualTo(imageDescription);

        verify(databaseWrapper, times(1)).create(bash, Bash.class);
    }

    @Test
    public void testCreateFail() throws Exception {
        when(databaseWrapper.create(bash, Bash.class)).thenThrow(new RuntimeException("database fail"));

        Optional<Bash> bashOptional = bashRepository.create(bash);

        assertThat(bashOptional.isPresent()).isFalse();

        verify(databaseWrapper, times(1)).create(bash, Bash.class);
    }

    @Test
    public void testGetByIdPass() throws Exception {
        String id = UUID.randomUUID().toString();
        bash.setId(id);
        when(databaseWrapper.getById(id, Bash.class)).thenReturn(bash);

        Optional<Bash> bashOptional = bashRepository.getById(id);

        assertThat(bashOptional.isPresent());
        assertThat(bashOptional.get()).isEqualTo(bash);
        verify(databaseWrapper, times(1)).getById(id, Bash.class);
    }

    @Test
    public void testGetByIdFail() throws Exception {
        String id = UUID.randomUUID().toString();
        bash.setId(id);
        when(databaseWrapper.getById(id, Bash.class)).thenThrow(new RuntimeException("database failed"));

        Optional<Bash> bashOptional = bashRepository.getById(id);

        assertThat(bashOptional.isPresent()).isFalse();
        verify(databaseWrapper, times(1)).getById(id, Bash.class);
    }

    @Test
    public void testSearchPass() throws Exception {
        String id = UUID.randomUUID().toString();
        bash.setId(id);

        Map<String, Object> query = Collections.singletonMap("id", id);

        when(databaseWrapper.search(query, Bash.class)).thenReturn(Collections.singletonList(bash));

        List<Bash> bashList = bashRepository.search(query);

        assertThat(bashList.isEmpty()).isFalse();
        assertThat(bashList.get(0)).isEqualTo(bash);
        verify(databaseWrapper, times(1)).search(query, Bash.class);
    }

    @Test
    public void testSearchFail() throws Exception {
        Map<String, Object> query = Collections.singletonMap("id", UUID.randomUUID().toString());
        when(databaseWrapper.search(query, Bash.class)).thenThrow(new RuntimeException("database fail"));

        List<Bash> bashList = bashRepository.search(query);

        assertThat(bashList.isEmpty()).isTrue();
        verify(databaseWrapper, times(1)).search(query, Bash.class);
    }

    @Test
    public void testDeletePass() throws Exception {
        String id = UUID.randomUUID().toString();
        when(databaseWrapper.findAndUpdate(
                anyMapOf(String.class, Object.class),
                anyMapOf(String.class, Object.class),
                eq(Bash.class)))
                .thenReturn(1L);

        Long count = bashRepository.delete(id);

        assertThat(count).isEqualTo(1L);
        verify(databaseWrapper, times(1)).findAndUpdate(
                anyMapOf(String.class, Object.class),
                anyMapOf(String.class, Object.class),
                eq(Bash.class));
    }

    @Test
    public void testDeleteFail() throws Exception {
        String id = UUID.randomUUID().toString();
        when(databaseWrapper.findAndUpdate(
                anyMapOf(String.class, Object.class),
                anyMapOf(String.class, Object.class),
                eq(Bash.class)))
                .thenThrow(new RuntimeException("database fail"));

        Long count = bashRepository.delete(id);

        assertThat(count).isEqualTo(-1L);
        verify(databaseWrapper, times(1)).findAndUpdate(
                anyMapOf(String.class, Object.class),
                anyMapOf(String.class, Object.class),
                eq(Bash.class));
    }

    @Test
    public void testUpdatePass() throws Exception {
        String id = UUID.randomUUID().toString();

        when(databaseWrapper.findAndUpdate(
                anyMapOf(String.class, Object.class),
                anyMapOf(String.class, Object.class),
                eq(Bash.class)))
                .thenReturn(1L);

        Long count = bashRepository.update(id, bash);

        assertThat(count).isEqualTo(1L);

        verify(databaseWrapper, times(1)).findAndUpdate(
                anyMapOf(String.class, Object.class),
                anyMapOf(String.class, Object.class),
                eq(Bash.class));
    }

    @Test
    public void testUpdateFail() throws Exception {
        String id = UUID.randomUUID().toString();
        when(databaseWrapper.findAndUpdate(
                anyMapOf(String.class, Object.class),
                anyMapOf(String.class, Object.class),
                eq(Bash.class)))
                .thenThrow(new RuntimeException("database fail"));

        Long count = bashRepository.update(id, bash);

        assertThat(count).isEqualTo(-1L);
        verify(databaseWrapper, times(1)).findAndUpdate(
                anyMapOf(String.class, Object.class),
                anyMapOf(String.class, Object.class),
                eq(Bash.class));
    }
}