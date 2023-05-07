package au.com.promedicus.admissions.dao;

import au.com.promedicus.admissions.model.Admission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdmissionRepository extends JpaRepository<Admission,Long> {
}
