package uit.ensak.dish_wish_frontend.Command;

import java.util.List;

import okhttp3.ResponseBody;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import uit.ensak.dish_wish_frontend.Models.Auth.AuthenticationResponse;
import uit.ensak.dish_wish_frontend.Models.Auth.LoginPayload;
import uit.ensak.dish_wish_frontend.Models.Chef;
import uit.ensak.dish_wish_frontend.Models.Client;
import uit.ensak.dish_wish_frontend.Models.Command;
import uit.ensak.dish_wish_frontend.Models.Notification;
import uit.ensak.dish_wish_frontend.Models.Proposition;
import uit.ensak.dish_wish_frontend.Models.Rating;

public interface ApiService {
    @GET("clients/{id}")
    Call<Client> getUser(@Header("Authorization") String authToken, @Path("id") Long id);

    @GET("clients/email")
    Call<Client> getUserByEmail(@Header("Authorization") String authToken,@Body String email);
    @Multipart
    @POST("clients/becomeCook/{clientId}")
    Call<Chef> becomeCook(
            @Header("Authorization") String authToken,
            @Path("clientId") Long clientId,
            @Part MultipartBody.Part idCard,
            @Part MultipartBody.Part certificate
    );

    @DELETE("clients/delete")
    Call<Void> deleteUserAccount(@Header("Authorization") String authToken,@Body String email);

    @POST("clients/add")
    Call<Void> sendData(@Body Client client);

    @POST("commands/create")
    Call<Command> createCommand(@Header("Authorization") String authToken, @Body Command command);

    @GET("commands")
    Call<List<Command>> getCommands(@Header("Authorization") String authToken);

    @GET("commands/{id}")
    Call<Command> getCommandById(@Header("Authorization") String authToken, @Path("id") Long id);

    @POST("/propositions/offer")
    Call<Void> sendProposition(@Header("Authorization") String authToken,@Body Proposition proposition);

    @GET("propositions")
    Call<List<Proposition>> getPropositions(@Header("Authorization") String authToken);

    @PUT("commands/update/{id}")
    Call<Command> updateCommand(@Header("Authorization") String authToken,
            @Path("id") Long commandId,
            @Body Command command
    );
    @GET("chef-ratings/{chefId}")
    Call<Double> getChefRatings(@Path("chefId") long chefId, @Header("Authorization") String accessToken);

    @PUT("commands/{commandId}/assign/{chefId}")
    Call<Void> assignChefToCommand(
            @Path("commandId") long commandId,
            @Path("chefId") long chefId,
            @Header("Authorization") String accessToken
    );

    @DELETE("propositions/delete/{propositionId}")
    Call<Void> deleteProposition(@Header("Authorization") String authToken,@Path("propositionId") Long propositionId);

    @PUT("propositions/update/{propositionId}")
    Call<Proposition> updateProposition(
            @Path("propositionId") Long propositionId,
            @Body Proposition proposition,
            @Header("Authorization") String accessToken
    );
    @GET("propositions/client/{clientId}")
    Call<List<Proposition>> getPropositionsByClientId(@Header("Authorization") String authToken, @Path("clientId") Long clientId);

    @GET("commands/client/{clientId}")
    Call<List<Command>> getCommandsByClientId(@Header("Authorization") String authToken, @Path("clientId") long clientId);


    @GET("notifications/client/{clientId}")
    Call<List<Proposition>> getClientNotifications(
            @Header("Authorization") String authToken,
            @Path("clientId") long clientId
    );

    @GET("notifications/chef/{chefId}")
    Call<List<Command>> getChefNotifications(
            @Header("Authorization") String authToken,
            @Path("chefId") long chefId
    );

    @GET("notifications/chef/confirmed/{chefId}")
    Call<List<Command>> getChefConfirmedNotifications(
            @Header("Authorization") String authToken,
            @Path("chefId") long chefId
    );

    @Headers({"Accept: application/json"})
    @POST("auth/authenticate")
    Call<AuthenticationResponse> authentification(@Body LoginPayload loginPayload);

    @POST("auth/logout")
    Call<Void> logout(@Header("Authorization") String authToken);


}