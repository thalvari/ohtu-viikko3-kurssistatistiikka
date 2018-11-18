package ohtu;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException {
        // ÄLÄ laita githubiin omaa opiskelijanumeroasi
        String studentNr = "012345678";
        if (args.length > 0) {
            studentNr = args[0];
        }

        String subsUrl = "https://studies.cs.helsinki.fi/courses/students/" + studentNr + "/submissions";
        String subsBodyText = Request.Get(subsUrl).execute().returnContent().asString();
        String coursesUrl = "https://studies.cs.helsinki.fi/courses/courseinfo";
        String coursesBodyText = Request.Get(coursesUrl).execute().returnContent().asString();

        Gson mapper = new Gson();
        Submission[] subs = mapper.fromJson(subsBodyText, Submission[].class);
        CourseInfo[] courses = mapper.fromJson(coursesBodyText, CourseInfo[].class);

        System.out.println("opiskelijanumero " + studentNr + "\n");
        for (CourseInfo courseInfo : courses) {
            int totalHours = 0, totalExercises = 0, totalMaxExercises = 0;
            for (int maxExercises : courseInfo.getExercises()) {
                totalMaxExercises += maxExercises;
            }
            System.out.println(courseInfo.getFullName() + " " + courseInfo.getTerm() + " " + courseInfo.getYear() +
                    "\n");
            for (Submission submission : subs) {
                if (submission.getCourse().equals(courseInfo.getName())) {
                    int week = submission.getWeek();
                    List<Integer> exercises = submission.getExercises();
                    System.out.println("viikko " + week + ":");
                    String s = " tehtyjä tehtäviä " + exercises.size() + "/"
                            + courseInfo.getExercises().get(week) + " aikaa kului "
                            + submission.getHours() + " tehdyt tehtävät: ";
                    for (int i = 0; i < exercises.size(); i++) {
                        s += exercises.get(i);
                        if (i != exercises.size() - 1) {
                            s += ", ";
                        }
                    }
                    System.out.println(s);
                    totalHours += submission.getHours();
                    totalExercises += submission.getExercises().size();
                }
            }
            System.out.println("\nyhteensä: " + totalExercises + "/" + totalMaxExercises + " tehtävää " + totalHours
                    + " tuntia\n");
            double totalSubmissions = 0, totalSubmittedExercises = 0, totalHours2 = 0;;
            String statsUrl = "https://studies.cs.helsinki.fi/courses/" + courseInfo.getName() + "/stats";
            String statsBodyText = Request.Get(statsUrl).execute().returnContent().asString();
            JsonParser parser = new JsonParser();
            JsonObject parsedData = parser.parse(statsBodyText).getAsJsonObject();
            for (Map.Entry<java.lang.String, JsonElement> weekEntry : parsedData.entrySet()) {
                Map<String, Object> map = mapper.fromJson(weekEntry.getValue().toString(), Map.class);
                totalSubmissions += (double) map.get("students");
                totalSubmittedExercises += (double) map.get("exercise_total");
                totalHours2 += (double) map.get("hour_total");
            }
            System.out.println("kurssilla yhteensä " + (int) totalSubmissions + " palautusta, palautettuja tehtäviä "
                    + (int) totalSubmittedExercises + " kpl, aikaa käytetty yhteensä " + (int) totalHours2
                    + " tuntia\n");
        }
    }

}
