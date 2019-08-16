package com.jojos.bank.api;

import com.jojos.bank.money.Account;
import com.jojos.bank.money.Currency;
import com.jojos.bank.money.Transfer;
import com.jojos.bank.resource.Server;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

/**
 * Testing our resource api.
 *
 * @implNote In this unit test order matters! The order in which the tests are executed are important,
 * ie the insertions must be made before checking for sums, types etc.
 * The pattern followed is test1XXX, test2XXX, etc
 *
 * Created by karanikasg@gmail.com.
 */
public class ResourceApiTest {

    private static final Server server = new Server();

    private static Client client;
    private static WebTarget rootWebTarget;

    @BeforeClass
    public static void setUp() {
        server.start();
        client = ClientBuilder.newClient().register(JacksonFeature.class);
        rootWebTarget = client.target(server.getURI()).path("/api");
    }

    @AfterClass
    public static void cleanUp() {
        client.close();
        server.stop();
    }

    @Test
    public void testCreateAccounts() {
        Account account1 = new Account(new BigDecimal("5099"), Currency.EURO);
        Account account2 = new Account(new BigDecimal("9999"), Currency.EURO);
        Response response1 = rootWebTarget.path("create")
                .request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(account1, MediaType.APPLICATION_JSON_TYPE));
        Assert.assertEquals(200, response1.getStatus());
        int accountId1 = response1.readEntity(Integer.class);

        Response response2 = rootWebTarget.path("create")
                .request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(account2, MediaType.APPLICATION_JSON_TYPE));
        Assert.assertEquals(200, response2.getStatus());
        int accountId2 = response2.readEntity(Integer.class);

        Assert.assertEquals(accountId1 + 1, accountId2);
    }

    @Test
    public void testTransferBetweenAccount() {
        Account accountFrom = new Account(new BigDecimal("15099.01"), Currency.EURO);
        Account accountTo = new Account(new BigDecimal("30001.99"), Currency.EURO);
        Response createFromAccountResponse = rootWebTarget.path("create")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(accountFrom, MediaType.APPLICATION_JSON_TYPE));
        Assert.assertEquals(200, createFromAccountResponse.getStatus());
        int accountIdFrom = createFromAccountResponse.readEntity(Integer.class);

        Response createToAccountResponse = rootWebTarget.path("create")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(accountTo, MediaType.APPLICATION_JSON_TYPE));
        Assert.assertEquals(200, createToAccountResponse.getStatus());
        int accountIdTo = createToAccountResponse.readEntity(Integer.class);

        Assert.assertEquals(accountIdFrom + 1, accountIdTo);

        Transfer transfer = new Transfer(accountIdFrom, accountIdTo, "99.01");
        rootWebTarget.path("transfer")
                .request()
                .post(Entity.entity(transfer, MediaType.APPLICATION_JSON_TYPE));

        Response getFromAccountResponse = rootWebTarget
                .path("account/" + accountIdFrom)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        Response getToAccountResponse = rootWebTarget
                .path("account/" + accountIdTo)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        Assert.assertEquals(200, getFromAccountResponse.getStatus());
        Assert.assertEquals(200, getToAccountResponse.getStatus());

        BigDecimal expectedFrom = new BigDecimal("15000");
        BigDecimal expectedTo = new BigDecimal("30101");

        Assert.assertEquals(
                getFromAccountResponse.readEntity(Account.class).getBalance().compareTo(expectedFrom), 0);
        Assert.assertEquals(
                getToAccountResponse.readEntity(Account.class).getBalance().compareTo(expectedTo), 0);

    }

    @Test
    public void testCorrectEuroScaleForPositives() {
        Account account = new Account(new BigDecimal("98.999999"), Currency.EURO);
        Response createAccountResponse = rootWebTarget.path("create")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(account, MediaType.APPLICATION_JSON_TYPE));
        Assert.assertEquals(200, createAccountResponse.getStatus());
        int accountId = createAccountResponse.readEntity(Integer.class);

        Response getAccountResponse = rootWebTarget
                .path("account/" + accountId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        Assert.assertEquals(200, getAccountResponse.getStatus());

        BigDecimal expectedBalance = new BigDecimal("99");
        BigDecimal actual = getAccountResponse.readEntity(Account.class).getBalance();

        Assert.assertEquals(actual.compareTo(expectedBalance), 0);


        account = new Account(new BigDecimal("10.433333"), Currency.EURO);
        createAccountResponse = rootWebTarget.path("create")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(account, MediaType.APPLICATION_JSON_TYPE));
        Assert.assertEquals(200, createAccountResponse.getStatus());
        accountId = createAccountResponse.readEntity(Integer.class);

        getAccountResponse = rootWebTarget
                .path("account/" + accountId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        Assert.assertEquals(200, getAccountResponse.getStatus());

        expectedBalance = new BigDecimal("10.43");
        actual = getAccountResponse.readEntity(Account.class).getBalance();

        Assert.assertEquals(actual.compareTo(expectedBalance), 0);

    }

    @Test
    public void testCorrectEuroScaleForNegatives() {
        Account account = new Account(new BigDecimal("-98.999999"), Currency.EURO);
        Response createAccountResponse = rootWebTarget.path("create")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(account, MediaType.APPLICATION_JSON_TYPE));
        Assert.assertEquals(200, createAccountResponse.getStatus());
        int accountId = createAccountResponse.readEntity(Integer.class);

        Response getAccountResponse = rootWebTarget
                .path("account/" + accountId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        Assert.assertEquals(200, getAccountResponse.getStatus());

        BigDecimal expectedBalance = new BigDecimal("-99");
        BigDecimal actual = getAccountResponse.readEntity(Account.class).getBalance();

        Assert.assertEquals(actual.compareTo(expectedBalance), 0);


        account = new Account(new BigDecimal("-10.433333"), Currency.EURO);
        createAccountResponse = rootWebTarget.path("create")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(account, MediaType.APPLICATION_JSON_TYPE));
        Assert.assertEquals(200, createAccountResponse.getStatus());
        accountId = createAccountResponse.readEntity(Integer.class);

        getAccountResponse = rootWebTarget
                .path("account/" + accountId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        Assert.assertEquals(200, getAccountResponse.getStatus());

        expectedBalance = new BigDecimal("-10.43");
        actual = getAccountResponse.readEntity(Account.class).getBalance();

        Assert.assertEquals(actual.compareTo(expectedBalance), 0);
    }

}