package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/subject")
public class SubjectController {
    @Autowired
    SubjectRepository subjectRepository;

    //CREATE
    @RequestMapping(method = RequestMethod.POST)
    public String addSubject(@RequestBody Subject subject) {
        boolean existsByName = subjectRepository.existsByName(subject.getName());
        if (existsByName)
            return "This subject already exist";
        subjectRepository.save(subject);
        return "Subject added";
    }

    //READ

    @GetMapping
    public List<Subject> getSubjects() {
        return subjectRepository.findAll();
    }

    //UPDATE
    @PutMapping("/{id}")
    public String editSubject(@PathVariable Integer id, @RequestBody Subject subject) {
        Optional<Subject> optionalHotel = subjectRepository.findById(id);
        if (optionalHotel.isPresent()) {
            Subject editingSubject = optionalHotel.get();
            editingSubject.setName(subject.getName());
            subjectRepository.save(editingSubject);
            return "Successfully edited";
        }
        return "Subject not found";
    }


    //DELETE

    @DeleteMapping("/{id}")
    public String deleteSubject(@PathVariable Integer id) {
        subjectRepository.deleteById(id);
        boolean deleted = subjectRepository.existsById(id);
        if (deleted) {
            return "Successfully deleted";
        } else {
            return "Subject not found";
        }
    }

}
