package de.hatoka.tournament.internal.app.servlets;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import de.hatoka.common.capi.app.FrameRenderer;
import de.hatoka.common.capi.app.servlet.AbstractService;
import de.hatoka.tournament.capi.business.TournamentBusinessFactory;
import de.hatoka.tournament.internal.app.actions.RankAction;
import de.hatoka.tournament.internal.app.models.RankVO;
import de.hatoka.tournament.internal.app.models.TournamentRankModel;

@Path("/tournament/{id}/ranks")
public class TournamentRankService extends AbstractService
{
    private static final String RESOURCE_PREFIX = "de/hatoka/tournament/internal/templates/app/";
    public static final String METHOD_NAME_LIST = "list";

    @PathParam("id")
    private String tournamentID;

    public TournamentRankService()
    {
        super(RESOURCE_PREFIX);
    }

    private RankAction getRankAction()
    {
        TournamentBusinessFactory factory = getInstance(TournamentBusinessFactory.class);
        return new RankAction(getUserRef(), tournamentID, factory);
    }

    private Response redirect(String methodName)
    {
        return redirect(methodName, tournamentID);
    }

    @POST
    @Path("/actionList")
    public Response actionPlayerList(@FormParam("rankID") List<String> identifiers, @FormParam("delete") String deleteButton, @FormParam("save") String saveButton,
                    @FormParam("firstPosition_new") String firstPosition, @FormParam("lastPosition_new") String lastPosition, @FormParam("amount_new") String amount,
                    @FormParam("percentage_new") String percentage)
    {
        if (isButtonPressed(deleteButton))
        {
            return deleteRanks(identifiers);
        }
        if (isButtonPressed(saveButton))
        {
            return createRank(firstPosition, lastPosition, amount, percentage);
        }
        return redirect(METHOD_NAME_LIST);
    }

    @POST
    @Path("/create")
    public Response createRank(@FormParam("firstPosition_new") String firstPosition, @FormParam("lastPosition_new") String lastPosition, @FormParam("amount_new") String amount,
                    @FormParam("percentage_new") String percentage)
    {
        RankAction action = getRankAction();
        try
        {
            runInTransaction(() -> action.createRank(firstPosition, lastPosition, amount, percentage));
        }
        catch(Exception e)
        {
            render(e);
        }
        return redirect(METHOD_NAME_LIST);
    }

    @GET
    @Path("/list.html")
    public Response list()
    {
        RankAction action = getRankAction();
        final TournamentRankModel model = action.getTournamentRankModel(getUriBuilder(TournamentListService.class, METHOD_NAME_LIST).build());
        model.getPrefilled().add(new RankVO("new"));
        try
        {
            String content = renderStyleSheet(model, "tournament_ranks.xslt", getXsltProcessorParameter("tournament"));
            return Response.status(200).entity(renderFrame(content, "ranks")).build();
        }
        catch(IOException e)
        {
            return render(e);
        }
    }

    @POST
    @Path("/delete")
    public Response deleteRanks(@FormParam("rankID") List<String> identifiers)
    {
        RankAction action = getRankAction();
        runInTransaction(() -> action.deleteRanks(identifiers));
        return redirect(METHOD_NAME_LIST);
    }

    private String renderFrame(String content, String subItem)
    {
        return getInstance(FrameRenderer.class).renderFame(content, "tournament", tournamentID, subItem);
    }
}