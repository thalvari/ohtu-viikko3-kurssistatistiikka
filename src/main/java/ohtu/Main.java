package ohtu;

import com.google.gson.Gson;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.util.List;

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
        }
    }

}
