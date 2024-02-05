import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

import org.apache.commons.math3.distribution.NormalDistribution;

class Student {
    String name;
    double original_score;
    double adjusted_score;
}

class ScoreComparator implements Comparator<Student> {
    @Override
    public int compare(Student s1, Student s2) {
        return Double.compare(s1.original_score, s2.original_score);
    }
}

class ScoreAdjuster {

    public static void mapScoresToNormal(ArrayList<Student> students, double lower_bound, double upper_bound) {
        int num_students = students.size();
        double min_score = students.get(0).original_score;
        double max_score = students.get(num_students - 1).original_score;
        double range = max_score - min_score;

        NormalDistribution standard_normal = new NormalDistribution(0, 1);

        for (Student student : students) {
            double cumulative_prob = (student.original_score - min_score) / range;
            cumulative_prob = Math.max(0.0000001, Math.min(0.9999999, cumulative_prob));
            double z = standard_normal.inverseCumulativeProbability(cumulative_prob);
            double mean = (upper_bound + lower_bound) / 2;
            double stddev = (upper_bound - lower_bound) / 6;
            student.adjusted_score = z * stddev + mean;
            student.adjusted_score = Math.round(student.adjusted_score * 2) / 2.0;
            student.adjusted_score = Math.max(lower_bound, Math.min(upper_bound, student.adjusted_score));
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入赋分上界：");
        double upper_bound = scanner.nextDouble();
        System.out.print("请输入赋分下界：");
        double lower_bound = scanner.nextDouble();
        System.out.print("请输入学生人数：");
        int num_students = scanner.nextInt();

        ArrayList<Student> students = new ArrayList<>();

        for (int i = 0; i < num_students; ++i) {
            System.out.print("请输入第" + (i + 1) + "位学生的姓名与成绩，姓名与成绩使用一个空格隔开：");
            Student student = new Student();
            student.name = scanner.next();
            student.original_score = scanner.nextDouble();
            students.add(student);
        }

        students.sort(new ScoreComparator());

        mapScoresToNormal(students, lower_bound, upper_bound);

        System.out.println("\n赋分成功！");
        for (Student student : students) {
            System.out.println(student.name + "\t原始分：" + student.original_score + "\t赋分：" + student.adjusted_score);
        }

        scanner.close();
    }
}
