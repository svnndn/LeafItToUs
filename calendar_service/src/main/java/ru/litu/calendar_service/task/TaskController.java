package ru.litu.calendar_service.task;

import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.litu.calendar_service.dto.ServiceResponseDto;
import ru.litu.calendar_service.task.service.TaskService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping()
@RequiredArgsConstructor
public class TaskController {

    private TaskService taskService;

    @GetMapping("/calendar/{date}/{userId}")
    public ResponseEntity<Object> displayTasksByClick(@PathVariable("date") int date, @PathVariable("userId") int userId) {
        List<Task> taskList = taskService.findByDateAndUserId(date, userId);

        // AJAX cannot recognise boolean, but isComplete is needed to set task background
        // so below is to create a list of int. 1 for complete, 0 for incomplete
        ArrayList<Integer> isCompleteList = new ArrayList<>();
        for (Task t : taskList) {
            if (t.isComplete()) {
                isCompleteList.add(1);
            } else
                isCompleteList.add(0);
        }
        ServiceResponseDto<List<Task>> list = new ServiceResponseDto<>("success", taskList, isCompleteList);
        return new ResponseEntity<Object>(list, HttpStatus.OK);
    }

    @GetMapping("/calendar/displayDetail/{taskId}")
    public ResponseEntity<Object> displayDetail(@PathVariable("taskId") long id) {
        Task task = taskService.getTask(id);
        ServiceResponseDto<Task> t = new ServiceResponseDto<>("success", task, null);
        return new ResponseEntity<Object>(t, HttpStatus.OK);
    }

    @PostMapping("/calendar/addTask/{userId}")
    public String save(@PathVariable("userId") long id, HttpServletRequest req, HttpServletResponse res) {
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        String type = req.getParameter("type");

        System.out.println(name + description + type);

        String dateWithSpace = req.getParameter("date");
        int date = processDateString(dateWithSpace);

        Task task;

        if (type.equals("assessment")) {
            task = new Task(name, date, description, false, id);
            taskService.save(task);
        }
        else {
            int[] dayInterval = new int[] {0, 1, 2, 4, 7, 15};
            for (int i : dayInterval) {
                task = new Task(name, date+i, description, false, id);
                taskService.save(task);
            }
        }

        return "redirect:/calendar";
        // hides form popup, and goes back to calendar main page
    }

    @RequestMapping("/calendar/delete/{taskId}")
    public String deleteTask(@PathVariable("taskId") long taskId) {
        taskService.deleteTask(taskId);
        return "redirect:/calendar";
    }

    @RequestMapping("/calendar/complete/{taskId}/{isComplete}")
    public String completeTask(@PathVariable("taskId") long taskId, @PathVariable("isComplete") boolean isComplete) {
        taskService.updateIsComplete(taskId, !isComplete);
        return "redirect:/calendar";
    }

    @PostMapping("/calendar/edit/{taskId}")
    public String editTask(@PathVariable("taskId") long taskId, HttpServletRequest req) {
        String newName = req.getParameter("name");
        String description = req.getParameter("description");

        Task task = taskService.getTask(taskId);
        task.setName(newName);
        task.setDescription(description);

        taskService.save(task);

        return "redirect:/calendar";
    }

    private int processDateString(String inputDate) {
        // input is in format of 2 3 2022: for 2nd March 2022
        String[] splitted = inputDate.split(" ");
        String day = splitted[0].length() == 1 ? "0"+splitted[0] : splitted[0];
        String month = splitted[1].length() == 1 ? "0"+splitted[1] : splitted[1];

        // processed to be 20220302
        return Integer.parseInt(splitted[2]+month+day);
    }
}
