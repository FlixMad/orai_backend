package com.ovengers.etcservice.service;

import com.ovengers.etcservice.entity.Attachment;
import com.ovengers.etcservice.repository.AttachmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;

    @Transactional
    public Attachment createAttachment(Attachment attachment) {
        return attachmentRepository.save(attachment);
    }

    @Transactional(readOnly = true)
    public List<Attachment> getAllAttachments() {
        return attachmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Attachment getAttachmentById(String id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attachment not found with id: " + id));
    }

    @Transactional
    public Attachment updateAttachment(String id, Attachment updatedAttachment) {
        Attachment existingAttachment = getAttachmentById(id);
        existingAttachment.setFileUrl(updatedAttachment.getFileUrl());
        existingAttachment.setType(updatedAttachment.getType());
        existingAttachment.setMessageId(updatedAttachment.getMessageId());
        existingAttachment.setTaskId(updatedAttachment.getTaskId());
        return attachmentRepository.save(existingAttachment);
    }

    @Transactional
    public void deleteAttachment(String id) {
        if (!attachmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Attachment not found with id: " + id);
        }
        attachmentRepository.deleteById(id);
    }
}
