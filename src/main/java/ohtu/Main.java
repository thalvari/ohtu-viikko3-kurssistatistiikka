package ohtu;

import com.google.gson.Gson;
import org.apache.http.client.fluent.Request;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        // ÄLÄ laita githubiin omaa opiskelijanumeroasi
        String studentNr = "012345678";
        if (args.length > 0) {
            studentNr = args[0];
        }

        String url = "https://studies.cs.helsinki.fi/courses/students/" + studentNr + "/submissions";

        String bodyText = Request.Get(url).execute().returnContent().asString();

//        System.out.println("json-muotoinen data:");
//        System.out.println(bodyText);

        Gson mapper = new Gson();
        Submission[] subs = mapper.fromJson(bodyText, Submission[].class);
        int totalHours = 0, totalExercises = 0;
        for (Submission submission : subs) {
            totalHours += submission.getHours();
            totalExercises += submission.getExercises().size();
        }

        System.out.println("opiskelijanumero " + studentNr + "\n");
        for (Submission submission : subs) {
            System.out.println(" " + submission);
        }
        System.out.println("\nyhteensä: " + totalExercises + " tehtävää " + totalHours + " tuntia");
    }

}
