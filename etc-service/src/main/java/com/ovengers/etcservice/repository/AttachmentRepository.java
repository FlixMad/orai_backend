package com.ovengers.etcservice.repository;

import com.ovengers.etcservice.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, String> {
}
