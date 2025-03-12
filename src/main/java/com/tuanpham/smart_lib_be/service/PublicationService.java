package com.tuanpham.smart_lib_be.service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuanpham.smart_lib_be.domain.*;
import com.tuanpham.smart_lib_be.domain.Response.PublicationRes;
import com.tuanpham.smart_lib_be.domain.Response.ResultPaginationDTO;
import com.tuanpham.smart_lib_be.mapper.PublicationMapper;
import com.tuanpham.smart_lib_be.repository.*;
import com.tuanpham.smart_lib_be.util.error.IdInvalidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicationService {
    private static final Logger log = LoggerFactory.getLogger(PublicationService.class);
    private final PublicationRepository publicationRepository;
    private final PublicationMapper publicationMapper;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final TopicRepository topicRepository;
    private final PublisherRepository publisherRepository;
    private final LanguageRepository languageRepository;
    private final WarehouseRepository warehouseRepository;
    private final CartUserRepository cartUserRepository;
    private final RegistrationUniqueRepository registrationUniqueRepository;
    private final WebClient webClient = WebClient.create();

    public PublicationService(PublicationRepository publicationRepository, PublicationMapper publicationMapper,
                              AuthorRepository authorRepository, CategoryRepository categoryRepository,
                              TopicRepository topicRepository, PublisherRepository publisherRepository,
                              LanguageRepository languageRepository, WarehouseRepository warehouseRepository,
                              CartUserRepository cartUserRepository, RegistrationUniqueRepository registrationUniqueRepository) {
        this.publicationRepository = publicationRepository;
        this.publicationMapper = publicationMapper;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.topicRepository = topicRepository;
        this.publisherRepository = publisherRepository;
        this.languageRepository = languageRepository;
        this.warehouseRepository = warehouseRepository;
        this.cartUserRepository = cartUserRepository;
        this.registrationUniqueRepository = registrationUniqueRepository;
    }

    public boolean handlePublicationExist(String name) {
        return this.publicationRepository.existsByName(name);
    }

    public void createDataPublication(Publication publication){
        // check authors
        if (publication.getAuthors() != null) {
            List<String> listAuthorIds = publication.getAuthors().stream().map(a -> a.getId())
                    .collect(Collectors.toList());
            List<Author> dbAuthors = this.authorRepository.findByIdIn(listAuthorIds);
            publication.setAuthors(dbAuthors);
        }
        //check categories
        if (publication.getCategories() != null) {
            List<String> listCategoryIds = publication.getCategories().stream().map(c -> c.getId())
                    .collect(Collectors.toList());
            List<Category> dbCategories = this.categoryRepository.findByIdIn(listCategoryIds);
            publication.setCategories(dbCategories);
        }

        // check topics
        if (publication.getTopics() != null) {
            List<String> listTopicIds = publication.getTopics().stream().map(t -> t.getId())
                    .collect(Collectors.toList());
            List<Topic> dbTopics = this.topicRepository.findByIdIn(listTopicIds);
            publication.setTopics(dbTopics);
        }

        // check publisher
        if (publication.getPublisher() != null) {
            Publisher dbPublisher = this.publisherRepository.findById(publication.getPublisher().getId()).orElse(null);
            publication.setPublisher(dbPublisher);
        }

        //check language
        if (publication.getLanguage() != null) {
            Language dbLanguage = this.languageRepository.findById(publication.getLanguage().getId()).orElse(null);
            publication.setLanguage(dbLanguage);
        }

        // check warehouse
        if (publication.getWarehouse() != null) {
            Warehouse dbWarehouse = this.warehouseRepository.findById(publication.getWarehouse().getId()).orElse(null);
            publication.setWarehouse(dbWarehouse);
        }
    }

    public void updateDataPublication(Publication publicationExist, Publication current){
        // check authors
        if (current.getAuthors() != null) {
            List<String> listAuthorIds = current.getAuthors().stream().map(a -> a.getId())
                    .collect(Collectors.toList());
            List<Author> dbAuthors = this.authorRepository.findByIdIn(listAuthorIds);
            publicationExist.setAuthors(dbAuthors);
        }
        //check categories
        if (current.getCategories() != null) {
            List<String> listCategoryIds = current.getCategories().stream().map(c -> c.getId())
                    .collect(Collectors.toList());
            List<Category> dbCategories = this.categoryRepository.findByIdIn(listCategoryIds);
            publicationExist.setCategories(dbCategories);
        }

        // check topics
        if (current.getTopics() != null) {
            List<String> listTopicIds = current.getTopics().stream().map(t -> t.getId())
                    .collect(Collectors.toList());
            List<Topic> dbTopics = this.topicRepository.findByIdIn(listTopicIds);
            publicationExist.setTopics(dbTopics);
        }

        // check publisher
        if (current.getPublisher() != null) {
            Publisher dbPublisher = this.publisherRepository.findById(current.getPublisher().getId()).orElse(null);
            publicationExist.setPublisher(dbPublisher);
        }

        //check language
        if (current.getLanguage() != null) {
            Language dbLanguage = this.languageRepository.findById(current.getLanguage().getId()).orElse(null);
            publicationExist.setLanguage(dbLanguage);
        }

        // check warehouse
        if (current.getWarehouse() != null) {
            Warehouse dbWarehouse = this.warehouseRepository.findById(current.getWarehouse().getId()).orElse(null);
            publicationExist.setWarehouse(dbWarehouse);
        }

        publicationExist.setName(current.getName());
        publicationExist.setPlaceOfPublication(current.getPlaceOfPublication());
        publicationExist.setYearOfPublication(current.getYearOfPublication());
        publicationExist.setPageCount(current.getPageCount());
        publicationExist.setSize(current.getSize());
        publicationExist.setClassify(current.getClassify());
        publicationExist.setIsbn(current.getIsbn());
        publicationExist.setIssn(current.getIssn());
        publicationExist.setDescription(current.getDescription());
    }

    public Publication handleCreatePublication(Publication publication) {
        createDataPublication(publication);
        return this.publicationRepository.save(publication);
    }
    public Publication handleUpdatePublication(Publication publication, Long id) throws IdInvalidException {
        Publication publicationExist = this.publicationRepository.findById(id).orElse(null);
        if (publicationExist == null) {
            throw new IdInvalidException("Ấn phẩm không tồn tại");
        }
        if(this.publicationRepository.existsByNameAndIdNot(publication.getName(), id)) {
            throw new IdInvalidException("Ấn phẩm đã tồn tại");
        }
        updateDataPublication(publicationExist, publication);
        return this.publicationRepository.save(publicationExist);
    }
    public Publication handleFindPublicationById(Long id) {
        return this.publicationRepository.findById(id).orElse(null);
    }

    public PublicationRes handleFindPublicationResById(Long id) {
        Publication publication = this.publicationRepository.findById(id).orElse(null);
        if (publication == null) {
            return null;
        }
        Long totalQuantityPub = this.cartUserRepository.sumQuantityByPublicationId(publication.getId());
        PublicationRes publicationRes = this.publicationMapper.toPublicationRes(publication);
        publicationRes.setTotalQuantity(totalQuantityPub);
        return publicationRes;
    }

    public ResultPaginationDTO handleGetAlLPublication(Specification<Publication> spec,
                                                    Pageable pageable) {
        Page<Publication> pagePublications = this.publicationRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pagePublications.getSize());
        meta.setTotal(pagePublications.getTotalElements());// amount of elements
        meta.setPages(pagePublications.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        // remove sensitive data
        List<PublicationRes> listPublications = pagePublications.getContent().stream().map(
                        p ->{
                            Long totalQuantityPub = this.cartUserRepository.sumQuantityByPublicationId(p.getId());
                            PublicationRes publicationRes = this.publicationMapper.toPublicationRes(p);
                            publicationRes.setTotalQuantity(totalQuantityPub);
                            return publicationRes;

                        })
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listPublications);
        return resultPaginationDTO;
    }

    public void handleDeletePublication(Long id) {
        this.publicationRepository.deleteById(id);
    }

    public ResultPaginationDTO handleGetRegistrationUniques(Specification<RegistrationUnique> spec,
                                                        Pageable pageable) {
        Page<RegistrationUnique> pageRegistrationUniques = this.registrationUniqueRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber()+1);
        meta.setPageSize(pageRegistrationUniques.getSize());
        meta.setTotal(pageRegistrationUniques.getTotalElements());// amount of elements
        meta.setPages(pageRegistrationUniques.getTotalPages());// amount of pages
        resultPaginationDTO.setMeta(meta);
        // remove sensitive data
        List<RegistrationUnique> listRegistrationUniques = pageRegistrationUniques.getContent().stream().map(
                        r -> r)
                .collect(Collectors.toList());
        resultPaginationDTO.setResult(listRegistrationUniques);
        return resultPaginationDTO;
    }

    public List<Publication> handleGetPublicationSuggestions(Long id) {
        WebClient webClient = WebClient.create("http://localhost:8000");

        // Gọi API và chờ kết quả JSON
        String jsonResponse = webClient.get()
                .uri("/recommendations/" + id)
                .retrieve()
                .bodyToMono(String.class)
                .block(); // Chờ lấy dữ liệu

        List<Publication> publications = new ArrayList<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode recommendedBooksNode = rootNode.get("recommended_books");

            int[] recommendedBooks = objectMapper.convertValue(recommendedBooksNode, int[].class);
            for (int publicationId : recommendedBooks) {
                Publication publication = this.publicationRepository.findById((long) publicationId).orElse(null);
                if (publication != null) {
                    log.info("Publication: " + publication.getName());
                    publications.add(publication);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return publications;
    }

    //update model suggestion publications
    //run cron job to update model (5PM everyday)
    @Scheduled(cron = "0 0 17 * * *")
    public void updateModelSuggestionPublications() {
        log.info("Updating model suggestion publications...");
        WebClient webClient = WebClient.create("http://localhost:8000");

        // Gọi API và chờ kết quả JSON
        String jsonResponse = webClient.post()
                .uri("/train")
                .retrieve()
                .bodyToMono(String.class)
                .block(); // Chờ lấy dữ liệu

        log.info("Model suggestion publications updated!");
    }

}
