package com.upgrad.quora.service.entity;


import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "QUESTION")
@NamedQueries({

    @NamedQuery(name = "questionByUuid", query = "select q from QuestionEntity q where q.uuid = :uuid "),
    @NamedQuery(name = "listofAllQuestions", query = "select q from QuestionEntity q "),
    @NamedQuery(name = "allQuestionsByUser", query = "SELECT q FROM QuestionEntity q JOIN q.userEntity u WHERE u.uuid = :uuid")

})
public class QuestionEntity implements Serializable {

  @Id
  @Column(name = "ID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "UUID")
  @NotNull
  @Size(max = 200)
  private String uuid;

  @Column(name = "CONTENT")
  @NotNull
  @Size(max = 500)
  private String content;

  @ManyToOne
  @JoinColumn(name = "USER_ID")
  private UserEntity userEntity;

  @OneToMany(mappedBy = "questionEntity")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private List<AnswerEntity> answerEntityList;

  @Column(name = "DATE")
  @NotNull
  private ZonedDateTime date;

  public Integer getId() {
    return id;
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

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public ZonedDateTime getDate() {
    return date;
  }

  public void setDate(ZonedDateTime date) {
    this.date = date;
  }

  public UserEntity getUserEntity() {
    return this.userEntity;
  }

  public void setUserEntity(UserEntity userEntity) {
    this.userEntity = userEntity;
  }

  public List<AnswerEntity> getAnswerEntityList() {
    return answerEntityList;
  }

  public void setAnswerEntityList(List<AnswerEntity> answerEntityList) {
    this.answerEntityList = answerEntityList;
  }

  @Override
  public boolean equals(Object obj) {
    return new EqualsBuilder().append(this, obj).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(this).hashCode();
  }

  /*@Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }*/
}