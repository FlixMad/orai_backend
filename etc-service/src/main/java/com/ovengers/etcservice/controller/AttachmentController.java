package com.ovengers.etcservice.controller;

import com.ovengers.etcservice.entity.Attachment;
import com.ovengers.etcservice.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping
    public ResponseEntity<Attachment> createAttachment(@RequestBody Attachment attachment) {
        Attachment createdAttachment = attachmentService.createAttachment(attachment);
        return ResponseEntity.ok(createdAttachment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Attachment> getAttachmentById(@PathVariable String id) {
        Attachment attachment = attachmentService.getAttachmentById(id);
        return ResponseEntity.ok(attachment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Attachment> updateAttachment(
            @PathVariable String id,
            @RequestBody Attachment updatedAttachment) {
        Attachment attachment = attachmentService.updateAttachment(id, updatedAttachment);
        return ResponseEntity.ok(attachment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable String id) {
        attachmentService.deleteAttachment(id);
        return ResponseEntity.noContent().build();
    }
}
