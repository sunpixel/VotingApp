using Microsoft.EntityFrameworkCore;

namespace DataPopulator
{
    public class AppDbContext : DbContext
    {
        public DbSet<User> Users { get; set; }
        public DbSet<Vote> Votes { get; set; }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            optionsBuilder.UseNpgsql("Host=localhost;Port=5234;Database=VotesDb;Username=postgres;Password=8o6i.V]B<pT8}yF1;");
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<User>(entity =>
            {
                entity.HasKey(a => a.Id);

                entity.Property(a => a.Id)
                .ValueGeneratedOnAdd();

                entity.Property(a => a.Name)
                .HasMaxLength(150);

                entity.Property(a => a.Email)
                .HasMaxLength(200);

                entity.Property(a => a.Password)
                .HasMaxLength(200);

                entity.Property(a => a.Vote);

                entity
                .HasMany(a => a.Votes)
                .WithMany(u => u.Users);
            });

            modelBuilder.Entity<Vote>(entity =>
            {
                entity.HasKey(a => a.Id);

                entity.Property(a => a.Id)
                .ValueGeneratedOnAdd();

                entity
                .HasMany(a => a.Users)
                .WithMany(u => u.Votes);
            });
        }
    }
}
