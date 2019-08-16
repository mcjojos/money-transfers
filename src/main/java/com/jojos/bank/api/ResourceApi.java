package com.jojos.bank.api;

import com.jojos.bank.money.Account;
import com.jojos.bank.money.Transfer;
import com.jojos.bank.service.TransferHandler;
import com.jojos.bank.util.AccountCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The class defining the rest api
 *
 * @author karanikasg@gmail.com.
 */
@Path("/api")
public class ResourceApi {

	private static final Logger log = LoggerFactory.getLogger(ResourceApi.class);

	private static final TransferHandler handler = TransferHandler.INSTANCE;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String get() {
		return "\n This is the REST API for money transfers between accounts via HTTPServer. " +
				"Use it with wisdom";
	}

	/**
	 * Get a single account identified by its id
	 * @param accountId the id of the account
	 * @return an account object associated with the id
	 */
	@GET @Path("account/{accountId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Account getAccount(@PathParam("accountId") int accountId) {
		log.debug("GET account/{}", accountId);

		return handler.getAccount(accountId);
	}

	/**
	 * Transfer a specific amount of money from one account to another
	 * @param transfer object holding info about the involved accounts and the amount to be transferred
	 * @return `200 OK` if the transfer was successfully executed or `404 Not Found` otherwise
	 */
	@POST @Path("transfer")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response transfer(Transfer transfer) {
		log.debug("POST transfer:{}", transfer);

		if (handler.execute(transfer)) {
			return Response.ok().build();
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}

	@POST @Path("create")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createAccount(Account account) {
		log.debug("POST create account:{}, {}", account);
		// normalize the account to have the right scale of the balance by creating a new account
		account = new Account(account.getBalance(), account.getCurrency());
		int accountId = handler.createAccount(account);
		return Response.ok().entity(accountId).build();
	}

	/**
	 * Used to produce some accounts for testing purposes
	 */
	@GET @Path("accounts/create_test")
	public Response createTestAccounts(@QueryParam("amount") int amount) {
		log.debug("GET accounts/create_test {}", amount);

		List<Integer> accountIds = AccountCreator.createAndStoreRandomTestAccounts(amount);

		String result = accountIds
				.stream()
				.map(String::valueOf)
				.collect(Collectors.joining(", ", "{", "}"));
		return Response.ok().entity(result).build();
	}


}