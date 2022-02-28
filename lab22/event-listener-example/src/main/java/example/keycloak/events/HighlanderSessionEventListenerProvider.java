package example.keycloak.events;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.adapter.InMemoryUserAdapter;
import org.jboss.logging.Logger;


/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
public class HighlanderSessionEventListenerProvider implements EventListenerProvider {

	private static final Logger LOG = Logger.getLogger(HighlanderSessionEventListenerProvider.class);

	private final KeycloakSession keycloakSession;

	public HighlanderSessionEventListenerProvider(KeycloakSession keycloakSession) {
		this.keycloakSession = keycloakSession;
	}

	@Override
	public void onEvent(Event event) {
		if (EventType.LOGIN.equals(event.getType())) {
			RealmModel realm = keycloakSession.getContext().getRealm();
			InMemoryUserAdapter user = new InMemoryUserAdapter(keycloakSession, realm, event.getUserId());

			LOG.warn(String.format("***** New login detected for the user %s for realm %s", event.getUserId(), realm.getName()));

			keycloakSession.sessions().getUserSessionsStream(realm, user).forEach(userSession -> {
				// remove all existing user sessions but the current one (last one wins)
				// this is HIGHLANDER MODE - there must only be one!
				if (!userSession.getId().equals(event.getSessionId())) {
					LOG.warn(String.format("***** Log-out the user %s", userSession.getLoginUsername()));
					keycloakSession.sessions().removeUserSession(realm, userSession);
				}
			});
		}
	}

	@Override
	public void onEvent(AdminEvent event, boolean includeRepresentation) {
	}

	@Override
	public void close() {
	}
}
