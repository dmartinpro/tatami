package fr.ippon.tatami.service;

import fr.ippon.tatami.AbstractCassandraTatamiTest;
import fr.ippon.tatami.domain.Status;
import fr.ippon.tatami.domain.User;
import fr.ippon.tatami.security.AuthenticationService;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.inject.Inject;
import java.util.Collection;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StatusUpdateServiceTest extends AbstractCassandraTatamiTest {

    @Inject
    public TimelineService timelineService;

    @Inject
    public StatusUpdateService statusUpdateService;

    @Test
    public void shouldPostStatus() throws Exception {
        String login = "userWhoPostStatus@ippon.fr";
        mockAuthenticationOnTimelineServiceWithACurrentUser("userWhoPostStatus@ippon.fr");
        mockAuthenticationOnStatusUpdateServiceWithACurrentUser("userWhoPostStatus@ippon.fr");
        String content = "Longue vie au Ch'ti Jug";

        statusUpdateService.postStatus(content);

        /* verify */
        Collection<Status> statusFromUserline = timelineService.getUserline("userWhoPostStatus", 10, null, null);
        assertThatNewTestIsPosted(login, content, statusFromUserline);

        Collection<Status> statusFromTimeline = timelineService.getTimeline(10, null, null);
        assertThatNewTestIsPosted(login, content, statusFromTimeline);

        Collection<Status> statusFromUserlineOfAFollower = timelineService.getUserline("userWhoReadStatus", 10, null, null);
        assertThat(statusFromUserlineOfAFollower.isEmpty(), is(true));

    }

    private void assertThatNewTestIsPosted(String login, String content, Collection<Status> statuses) {
        assertThat(statuses, notNullValue());
        assertThat(statuses.size(), is(1));
        Status status = (Status) statuses.toArray()[0];
        assertThat(status.getLogin(), is(login));
        assertThat(status.getContent(), is(content));
    }

    private void mockAuthenticationOnTimelineServiceWithACurrentUser(String login) {
        User authenticateUser = constructAUser(login);
        AuthenticationService mockAuthenticationService = mock(AuthenticationService.class);
        when(mockAuthenticationService.getCurrentUser()).thenReturn(authenticateUser);
        ReflectionTestUtils.setField(timelineService, "authenticationService", mockAuthenticationService);
    }

    private void mockAuthenticationOnStatusUpdateServiceWithACurrentUser(String login) {
        User authenticateUser = constructAUser(login);
        AuthenticationService mockAuthenticationService = mock(AuthenticationService.class);
        when(mockAuthenticationService.getCurrentUser()).thenReturn(authenticateUser);
        ReflectionTestUtils.setField(statusUpdateService, "authenticationService", mockAuthenticationService);
    }
}