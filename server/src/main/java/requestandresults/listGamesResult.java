package requestandresults;


import model.GameData;

import java.util.Collection;
public record listGamesResult(Collection<GameData> games) {
}
