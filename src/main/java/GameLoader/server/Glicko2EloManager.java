package GameLoader.server;

import GameLoader.common.Game;
import GameLoader.common.Utility.*;
import static GameLoader.common.Utility.IntDoublePair;

//it works, I guess
public class Glicko2EloManager implements EloManager{
    final double minimumRD=45;
    final double volatility=0.06;
    public Pair<Integer, Double> playerRatingChange(int eloP0, int eloP1, double rd1, double rd2, double result){
        rd1=Math.min(rd1,350);
        rd2=Math.min(rd2,350);

        //some would say it is beautiful, but I do not know who
        double ranking1 = (eloP0-1500)/173.7178;
        double ranking2 = (eloP1-1500)/173.7178;
        double deviation0 = rd1/173.7178;
        double deviation = rd2/173.7178; //opponent deviation
        double g = 1/(Math.sqrt(1+3*deviation*deviation/(3.14*3.14)));
        double E = 1/(1+Math.exp((-g)*(ranking1-ranking2)));
        double v = 1/((g*g)*E*(1-E));
        double tmp_deviation = Math.sqrt(deviation0*deviation0+volatility*volatility);
        deviation0 = 1/(Math.sqrt(1/tmp_deviation*tmp_deviation+1/v));
        ranking1 = ranking1 + deviation0*deviation0*g*(result-E);
        ranking1 = ranking1 * 173.7178 + 1500;
        deviation0 = 173.7178 * deviation0;
        int intRanking = (int)Math.round(ranking1);
        deviation0 = Math.min(45,deviation0); //deviation can't be too small because it would be ******
        return new Pair<>(intRanking, deviation0);
    }
    @Override
    public IntDoublePair calculate(int eloP0, int eloP1, double rd2, double rd1, Game.state result) {
        double score;
        if(result== Game.state.P0_WON){
            score=1;
        }
        else if(result==Game.state.P1_WON){
            score=0;
        }
        else score=0.5;
        //first player
        var tmp_1 = playerRatingChange(eloP0,eloP1,rd1,rd2,score);
        var tmp_2 = playerRatingChange(eloP1,eloP0,rd2,rd1,1-score);
        //second player
        return new IntDoublePair(
                tmp_1.first(),
                tmp_2.first(),
                tmp_1.second(),
                tmp_2.second()
        );
    }
}
