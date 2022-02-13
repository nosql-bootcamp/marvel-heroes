package errors;

import com.typesafe.config.Config;
import play.Environment;
import play.api.OptionalSourceMapper;
import play.api.UsefulException;
import play.api.routing.Router;
import play.http.DefaultHttpErrorHandler;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;
import scala.Option;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
public class ErrorHandler extends DefaultHttpErrorHandler {

    @Inject
    public ErrorHandler(
            Config config,
            Environment environment,
            Provider<Router> routes) {
        // See https://github.com/playframework/playframework/issues/10486
        super(config, environment, new OptionalSourceMapper(Option.empty()) , routes);
    }

    protected CompletionStage<Result> onProdServerError(
            RequestHeader request, UsefulException exception) {
        return CompletableFuture.completedFuture(
                Results.internalServerError("A server error occurred: " + exception.getMessage()));
    }


    @Override
    protected CompletionStage<Result> onNotFound(RequestHeader request, String message) {
        return CompletableFuture.completedFuture(
                Results.notFound(views.html.error.render(request, "Page not found", "Sorry, page not found!"))
        );
    }

    @Override
    protected CompletionStage<Result> onOtherClientError(RequestHeader request, int statusCode, String message) {
        return CompletableFuture.completedFuture(
                Results.notFound(views.html.error.render(request, message, message))
        );
    }

    protected CompletionStage<Result> onForbidden(RequestHeader request, String message) {
        return CompletableFuture.completedFuture(
                Results.forbidden("You're not allowed to access this resource."));
    }
}