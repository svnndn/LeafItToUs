package ru.litu.calendar_service.task;

import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.litu.calendar_service.dto.ServiceResponseDto;
import ru.litu.calendar_service.task.service.TaskService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping()
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/calendar/{date}/{userId}")
    public ResponseEntity<Object> displayTasksByClick(@PathVariable("date") int date, @PathVariable("userId") int userId) {
        String strDay = Integer.valueOf(date).toString().substring(6, 8);
        String strMonth = Integer.valueOf(date).toString().substring(4, 7);
        String strYear = Integer.valueOf(date).toString().substring(0, 4);

        LocalDateTime startTime = processDate(
                strDay + " " + strMonth + " " + strYear, "00:00"
        );
        LocalDateTime endTime = processDate(
                strDay + " " + strMonth + " " + strYear, "23:59"
        );
        List<Task> taskList = taskService.findByDateAndUserId(userId, startTime, endTime);

        // 1 for complete, 0 for incomplete
        ArrayList<Integer> isCompleteList = new ArrayList<>();
        for (Task t : taskList) {
            if (t.isComplete()) {
                isCompleteList.add(1);
            } else
                isCompleteList.add(0);
            log.info("display task with id: {}, name: {}", t.getId(), t.getName());
        }

        ServiceResponseDto<List<Task>> list = new ServiceResponseDto<>("success", taskList, isCompleteList);
        return new ResponseEntity<Object>(list, HttpStatus.OK);
    }

    @GetMapping("/calendar/displayDetail/{taskId}")
    public ResponseEntity<Object> displayDetail(@PathVariable("taskId") long id) {
        Task task = taskService.getTask(id);
        log.info("display detail task with id: {}", id);
        ServiceResponseDto<Task> t = new ServiceResponseDto<>("success", task, null);
        return new ResponseEntity<Object>(t, HttpStatus.OK);
    }

    @PostMapping("/calendar/addTask/{userId}")
    public String save(@PathVariable("userId") long id, HttpServletRequest req, HttpServletResponse res) {
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        String type = req.getParameter("type");

        System.out.println(name + description + type);

        String hour = req.getParameter("hours");
        if(hour.isEmpty()) {

        }
        String minute = req.getParameter("minutes");

        String dateWithSpace = req.getParameter("date");
        String time = hour + ":" + minute;
        LocalDateTime date = processDate(dateWithSpace, time);

        Task task;

        if (type.equals("forOneDay")) {
            task = new Task(name, date, description, false, id);
            taskService.save(task);
        } else if (type.equals("forWeek")) {
            int[] dayInterval = new int[] {0, 1, 2, 3, 4, 5, 6};
            for (int i : dayInterval) {
                task = new Task(name, date.plusDays(i), description, false, id);
                taskService.save(task);
            }
        } else {
            for (int i = 0; i < 30; i++) {
                task = new Task(name, date.plusDays(i), description, false, id);
                taskService.save(task);
            }
        }

//        if (result.hasErrors()) {
//            log.error(result.getAllErrors().toString());
//            return "error-page";
//        }
        log.info("save task with id: {}, name: {}", id, name);

        return "redirect:/calendar/" + id;
        // hides form popup, and goes back to calendar main page
    }

    @RequestMapping("/calendar/delete/{taskId}/{userId}")
    public String deleteTask(@PathVariable("taskId") long taskId, @PathVariable("userId") long userId) {
        taskService.deleteTask(taskId);
        log.info("user with id: {} delete task with id: {}", userId, taskId);
        return "redirect:/calendar/" + userId;
    }

    @RequestMapping("/calendar/complete/{taskId}/{isComplete}/{userId}")
    public String completeTask(@PathVariable("taskId") long taskId, @PathVariable("isComplete") boolean isComplete, @PathVariable("userId") boolean userId) {
        taskService.updateIsComplete(taskId, !isComplete);
        log.info("user with id: {} complete task with id: {}", userId, taskId);
        return "redirect:/calendar/" + userId;
    }

    @PostMapping("/calendar/edit/{taskId}/{userId}")
    public String editTask(@PathVariable("taskId") long taskId, @PathVariable("userId") long userId, BindingResult result, HttpServletRequest req) {
        String newName = req.getParameter("name");
        String description = req.getParameter("description");

        Task task = taskService.getTask(taskId);
        task.setName(newName);
        task.setDescription(description);

        taskService.save(task);

        if (result.hasErrors()) {
            return "error-page";
        }

        log.info("user with id: {} edit task with id: {}", userId, taskId);

        return "redirect:/calendar/" + userId;
    }

    @GetMapping("/actual")
    public ResponseEntity<Object> getAllActualTasks() {
        List<Task> taskList = taskService.findAllActualTasks();

        ArrayList<Integer> isCompleteList = new ArrayList<>();
        for (Task t : taskList) {
            isCompleteList.add(t.isComplete() ? 1 : 0);
            log.info("get actual task with id: {}, name: {}", t.getId(), t.getName());
        }

        return ResponseEntity.ok(
                new ServiceResponseDto<>("success", taskList, isCompleteList)
        );
    }

    private LocalDateTime processDate(String inputDate, String time) {
        // input is in format of 2 3 2022: for 2nd March 2022
        String[] splitted = inputDate.split(" ");
        String dayStr = splitted[0].length() == 1 ? "0"+splitted[0] : splitted[0];
        String monthStr = splitted[1].length() == 1 ? "0"+splitted[1] : splitted[1];
        String[] splittedTime = time.split(":");
        String hourStr = splittedTime[0];
        String minuteStr = splittedTime[1];

        int year = Integer.parseInt(splitted[2]);
        int month = Integer.parseInt(monthStr);
        int day = Integer.parseInt(dayStr);
        int hour = Integer.parseInt(hourStr);
        int minute = Integer.parseInt(minuteStr);

        // Создаем LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute);

        // Конвертируем в java.util.Date (если требуется)
        return localDateTime;
    }
}
