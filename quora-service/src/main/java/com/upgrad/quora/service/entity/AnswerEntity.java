package com.upgrad.quora.service.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "ANSWER", schema = "QUORA")
@NamedQueries({
    @NamedQuery(name = "getAnswerByUuid", query = "select ans from AnswerEntity ans where ans.uuid = :uuid"),
    @NamedQuery(name = "getAllAnswersToQuestion", query = "select ans from AnswerEntity ans where questionEntity.uuid = :uuid")
})
public class AnswerEntity implements Serializable {

  @Id
  @Column(name = "ID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "UUID")
  @Size(max = 200)
  @NotNull
  private String uuid;

  @Column(name = "ANS")
  @Size(max = 255)
  @NotNull
  private String answer;

  @Column(name = "DATE")
  @NotNull
  private ZonedDateTime date;

  /*@ManyToOne
  @JoinColumn(name = "USER_ID")
  @NotNull
  private UserEntity userEntity;

  @ManyToOne
  @JoinColumn(name = "QUESTION_ID")
  @NotNull
  private QuestionEntity questionEntity;*/

  @Version
  @Column(name = "VERSION", length = 19, nullable = false)
  private Long version;

  public Integer getId() {
    return id;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public ZonedDateTime getDate() {
    return date;
  }

  public void setDate(ZonedDateTime date) {
    this.date = date;
  }

  /*public UserEntity getUserEntity() {
    return userEntity;
  }

  public void setUserEntity(UserEntity userEntity) {
    this.userEntity = userEntity;
  }

  public QuestionEntity getQuestionEntity() {
    return questionEntity;
  }

  public void setQuestionEntity(QuestionEntity questionEntity) {
    this.questionEntity = questionEntity;
  }*/

  @Override
  public boolean equals(Object obj) {
    return new EqualsBuilder().append(this, obj).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(this).hashCode();
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

}
