package example.keycloak.requiredaction;

import org.keycloak.authentication.InitiatedActionSupport;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.services.validation.Validation;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.function.Consumer;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
public class MobileNumberRequiredAction implements RequiredActionProvider {

	public static final String PROVIDER_ID = "mobile-number-ra";

	private static final String MOBILE_NUMBER_FIELD = "mobile_number";

	@Override
	public InitiatedActionSupport initiatedActionSupport() {
		/*This method allow to call this action from URL by returning : */
		return InitiatedActionSupport.SUPPORTED;
	}

	@Override
	public void evaluateTriggers(RequiredActionContext context) {
		// if the user doesn't have a configured phone number yet
		if (context.getUser().getFirstAttribute(MOBILE_NUMBER_FIELD) == null) {
			context.getUser().addRequiredAction(PROVIDER_ID);
		}
	}

	@Override
	public void requiredActionChallenge(RequiredActionContext context) {
		// show initial form with the current user context
		context.challenge(createForm(context, null));
	}

	@Override
	public void processAction(RequiredActionContext context) {
		// called when the user submit the form

		UserModel user = context.getUser();

		MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
		// extract the user phone number from the HTTP Request/form
		String mobileNumber = formData.getFirst(MOBILE_NUMBER_FIELD);
		// validate the phone number
		if (mobileNumber.length() < 5) {
			context.challenge(createForm(context, form -> form.addError(new FormMessage(MOBILE_NUMBER_FIELD, "Invalid input"))));
			return;
		}
		// store the phone number in user's profile
		user.setSingleAttribute(MOBILE_NUMBER_FIELD, mobileNumber);

		// remove the required action for the current user
		user.removeRequiredAction(PROVIDER_ID);

		// display a success message
		context.success();
	}

	@Override
	public void close() {
	}
	// This method will create the form to display for the user in order to set/submit his phone number
	private Response createForm(RequiredActionContext context, Consumer<LoginFormsProvider> formConsumer) {
		LoginFormsProvider form = context.form();
		form.setAttribute("username", context.getUser().getUsername());

		String mobileNumber = context.getUser().getFirstAttribute(MOBILE_NUMBER_FIELD);
		form.setAttribute(MOBILE_NUMBER_FIELD, mobileNumber == null ? "" : mobileNumber);

		if (formConsumer != null) {
			formConsumer.accept(form);
		}

		return form.createForm("update-mobile-number.ftl");
	}

}
