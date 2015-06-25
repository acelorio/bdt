package com.stratio.specs;

import static org.testng.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.stratio.exceptions.DBException;
import com.stratio.tests.utils.ThreadProperty;
import com.thoughtworks.selenium.SeleniumException;

import cucumber.api.java.After;
import cucumber.api.java.Before;

/**
 * @author Javier Delgado
 * @author Hugo Dominguez
 */
public class HookGSpec extends BaseGSpec {

    public static final int ORDER_10 = 10;
    public static final int ORDER_20 = 20;
    public static final int PAGE_LOAD_TIMEOUT = 120;
    public static final int IMPLICITLY_WAIT = 10;
    public static final int SCRIPT_TIMEOUT = 30;

    /**
     * Default constructor.
     * 
     * @param spec
     */
    public HookGSpec(CommonG spec) {
        this.commonspec = spec;
    }

    /**
     * Clean the exception list.
     */
    @Before(order = 0)
    public void globalSetup() {
        commonspec.getLogger().info("Clearing exception list");
        commonspec.getExceptions().clear();
    }

    /**
     * Connect to Cassandra.
     */
    @Before(order = ORDER_10, value = "@C*")
    public void cassandraSetup() {
        commonspec.getLogger().info("Setting up C* client");
        commonspec.getCassandraClient().connect();
    }

    /**
     * Connect to MongoDB.
     */
    @Before(order = ORDER_10, value = "@MongoDB")
    public void mongoSetup() {
        commonspec.getLogger().info("Setting up MongoDB client");
        try {
            commonspec.getMongoDBClient().connect();
        } catch (DBException e) {
            fail(e.toString());
        }
    }

    /**
     * Connect to ElasticSearch.
     */
    @Before(order = ORDER_10, value = "@elasticsearch")
    public void elasticsearchSetup() {
        commonspec.getLogger().info("Setting up elasticsearch client");
        commonspec.getElasticSearchClient().connect();
    }

    /**
     * Connect to Aerospike.
     */
    @Before(order = ORDER_10, value = "@Aerospike")
    public void aerospikeSetup() {
        commonspec.getLogger().info("Setting up Aerospike client");
        commonspec.getAerospikeClient().connect();
    }

    /**
     * Connect to selenium.
     * 
     * @throws MalformedURLException
     */
    @Before(order = ORDER_10, value = "@web")
    public void seleniumSetup() throws MalformedURLException {
        String b = ThreadProperty.get("browser");
        if ("".equals(b)) {
            fail("Non available browsers");
        }
        String browser = b.split("_")[0];
        String version = b.split("_")[1];
        commonspec.setBrowserName(browser);
        commonspec.getLogger().info("Setting up selenium for {}", browser);

        DesiredCapabilities capabilities = null;
        if (browser.equalsIgnoreCase("chrome")) {
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("test-type");
            capabilities = DesiredCapabilities.chrome();
            capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        } else if (browser.equalsIgnoreCase("firefox")) {
            capabilities = DesiredCapabilities.firefox();
        } else if (browser.equalsIgnoreCase("phantomjs")) {
            capabilities = DesiredCapabilities.phantomjs();
        } else {
            commonspec.getLogger().error("Unknown browser: " + browser);
            throw new SeleniumException("Unknown browser: " + browser);
        }

        capabilities.setVersion(version);

        String grid = System.getProperty("SELENIUM.GRID", "127.0.0.1:4444");
        grid = "http://" + grid + "/wd/hub";
        commonspec.setDriver(new RemoteWebDriver(new URL(grid), capabilities));
        commonspec.getDriver().manage().timeouts().pageLoadTimeout(PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
        commonspec.getDriver().manage().timeouts().implicitlyWait(IMPLICITLY_WAIT, TimeUnit.SECONDS);
        commonspec.getDriver().manage().timeouts().setScriptTimeout(SCRIPT_TIMEOUT, TimeUnit.SECONDS);

        commonspec.getDriver().manage().deleteAllCookies();
        commonspec.getDriver().manage().window().maximize();

    }

    /**
     * Close selenium web driver.
     */
    @After(order = ORDER_20, value = "@web")
    public void seleniumTeardown() {
        if (commonspec.getDriver() != null) {
            commonspec.getLogger().info("Shutdown Selenium client");
            commonspec.getDriver().close();
            commonspec.getDriver().quit();
        }
    }

    /**
     * Close cassandra connection.
     */
    @After(order = ORDER_20, value = "@C*")
    public void cassandraTeardown() {
        commonspec.getLogger().info("Shutdown  C* client");
        try {
            commonspec.getCassandraClient().disconnect();
        } catch (DBException e) {
            fail(e.toString());
        }
    }

    /**
     * Close MongoDB Connection.
     */
    @After(order = ORDER_20, value = "@MongoDB")
    public void mongoTeardown() {
        commonspec.getLogger().info("Shutdown MongoDB client");
        commonspec.getMongoDBClient().disconnect();
    }

    /**
     * Close ElasticSearch connection.
     */
    @After(order = ORDER_20, value = "@elasticsearch")
    public void elasticsearchTeardown() {
        commonspec.getLogger().info("Shutdown elasticsearch client");
        try {
            commonspec.getElasticSearchClient().disconnect();
        } catch (DBException e) {
            fail(e.toString());
        }
    }

    /**
     * Close aerospike connection.
     */
    @After(order = ORDER_20, value = "@Aerospike")
    public void aerospikeTeardown() {
        commonspec.getLogger().info("Shutdown Aerospike client");
        try {
            commonspec.getAerospikeClient().disconnect();
        } catch (DBException e) {
            fail(e.toString());
        }
    }

    /**
     * Close logger.
     */
    @After(order = 0)
    public void teardown() {
        commonspec.getLogger().info("Ended running hooks");
    }
}