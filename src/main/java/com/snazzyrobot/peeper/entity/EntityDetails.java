   package com.snazzyrobot.peeper.entity;

   import java.time.OffsetDateTime;

   public interface EntityDetails {
       Long getId();
       void setId(Long id);

       OffsetDateTime getCreated();
       void setCreated(OffsetDateTime created);

       OffsetDateTime getModified();
       void setModified(OffsetDateTime modified);
   }