package com.saeedshahab.bashdown.core;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.saeedshahab.bashdown.auth.BashAuthenticator;
import com.saeedshahab.bashdown.models.User;
import com.saeedshahab.bashdown.repositories.BashRepository;
import com.saeedshahab.bashdown.repositories.UserRepository;
import com.saeedshahab.bashdown.resources.BashResource;
import com.saeedshahab.bashdown.resources.UserResource;
import com.saeedshahab.bashdown.wrappers.DatabaseWrapper;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.servlet.ServletProperties;

import javax.inject.Singleton;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;
import java.util.stream.Stream;

public class BashApplication extends Application<BashConfiguration> {

    public static void main(String[] args) throws Exception {
        new BashApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<BashConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets", "/"));
        bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)));
    }

    @Override
    public void run(BashConfiguration configuration, Environment environment) throws Exception {
        ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        environment.getApplicationContext()
                .getAttributes()
                .setAttribute(ServletProperties.SERVICE_LOCATOR, locator);

        Stream.of(
                BashResource.class,
                UserResource.class
                ).forEach(environment.jersey()::register);

        ObjectMapper mapper = Jackson.newObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        Class<?> databaseWrapper = Class.forName(configuration.getDatabaseClass());

        ServiceLocatorUtilities.bind(locator, new AbstractBinder() {
            @Override
            protected void configure() {
                bind(configuration).to(BashConfiguration.class);
                bind(mapper).to(ObjectMapper.class);
                bind(databaseWrapper).to(DatabaseWrapper.class).in(Singleton.class);

                Stream.of(
                        BashRepository.class,
                        UserRepository.class,
                        BashAuthenticator.class
                ).forEach(c -> bind(c).to(c));

            }
        });

        environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
                .setAuthenticator(locator.getService(BashAuthenticator.class))
                .setAuthorizer(((user, role) -> user.getRoles().contains(role)))
                .setRealm("Bashdown")
                .buildAuthFilter()));

        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));

        FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
        filter.setInitParameter("allowCredentials", "true");

        environment.healthChecks().register("Database", new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return locator.getService(DatabaseWrapper.class).health()
                        ? Result.healthy()
                        : Result.unhealthy("Database reports condition unhealthy!");
            }
        });
    }
}
