library("xlsx")

Postfices = c("A","K","U")
responses=data.frame()
for (postfix in Postfices) {
    file = read.xlsx(paste("./data/responses", postfix, ".xlsx", sep=""), 1)
    file = as.data.frame(file)
    responses = rbind(responses,file)
}

# Script to verify whether fields from expected_corrs are in names list
names = c("1. Страна",
          "2. Национальность",
          "3. Срок проживания",
          "4. Место жительства",
          "5. Планы на будущее",
          "6. Знание языка",
           "X7..Общение.на.работе",
           "X8..Общение.вне.работы",
           "X9..Дети.должны",
           "X10..Медиа.на.",
           "X11.1.Взятки.на.границе",
           "X11.2.Взятки.в.полиции",
           "X11.3.Расовая.дискриминация",
           "X11.4.Религия",
           "X11.5.Преступления",
           "X11.6.Работа",
           "X11.7.Низкая.зп",
           "X11.8.Работодатель.мудак",
           "X11.9.Тяжёлый.труд",
           "X11.10.Жильё",
           "X11.11.Медицина",
           "X11.12.Юри",
           "X11.13.Русский",
           "X11.14.Куда.с.проблемами",
           "X11.15.Где.работать..жить",
           "X12..Есть.диаспора.",
           "X13..А.вы.в.ней.",
           "14. Способ решения проблем",
           "X15..Гугл.работы",
           "X16..Гугл.жилья",
           "X17..НКО",
           "18. Создание семьи",
           "19. Получение гражданства",
           "20. Религиозность",
           "X21..Возраст",
           "22. Образование"
          )

names(responses) = names


# n_individuals = length(responses[,1])