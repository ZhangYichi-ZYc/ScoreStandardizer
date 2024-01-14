#include <iostream>
#include <vector>
#include <algorithm>
#include <cmath>
#include <boost/math/distributions/normal.hpp>

struct Student {
	std::string name;
	double original_score;
	double adjusted_score;
};

bool compareScores(const Student& s1, const Student& s2) {
	return s1.original_score < s2.original_score;
}

void mapScoresToNormal(std::vector<Student>& students, double lower_bound, double upper_bound) {
	size_t num_students = students.size();
	double min_score = students.front().original_score;
	double max_score = students.back().original_score;
	double range = max_score - min_score;

	boost::math::normal standard_normal(0, 1);

	for (Student& student : students) {
		double cumulative_prob = (student.original_score - min_score) / range;
		cumulative_prob = std::max(0.0000001, std::min(0.9999999, cumulative_prob));
		double z = boost::math::quantile(standard_normal, cumulative_prob);
		double mean = (upper_bound + lower_bound) / 2;
		double stddev = (upper_bound - lower_bound) / 6;
		student.adjusted_score = z * stddev + mean;
		student.adjusted_score = std::round(student.adjusted_score * 2) / 2;
		student.adjusted_score = std::max(lower_bound, std::min(upper_bound, student.adjusted_score));
	}
}


int main() {
	int num_students;
	double lower_bound, upper_bound;
	std::cout << "请输入赋分上界：";
	std::cin >> upper_bound;
	std::cout << "请输入赋分下界：";
	std::cin >> lower_bound;
	std::cout << "请输入学生人数：";
	std::cin >> num_students;

	std::vector<Student> students(num_students);

	for (int i = 0; i < num_students; ++i) {
		std::cout << "请输入第" << i + 1 << "位学生的姓名与成绩，姓名与成绩使用一个空格隔开：";
		std::cin >> students[i].name >> students[i].original_score;
	}

	std::sort(students.begin(), students.end(), compareScores);

	mapScoresToNormal(students, lower_bound, upper_bound);

	std::cout << "\n赋分成功！\n";
	for (const Student& student : students) {
		std::cout << student.name << "\t原始分：" << student.original_score << "\t赋分：" << student.adjusted_score << std::endl;
	}

	std::cout << "\n按回车键关闭窗口";
	std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
	std::cin.get();

	return 0;
}
